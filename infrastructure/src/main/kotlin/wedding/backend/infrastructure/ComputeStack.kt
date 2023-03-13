package wedding.backend.infrastructure

import software.amazon.awscdk.*
import software.amazon.awscdk.services.autoscaling.AutoScalingGroup
import software.amazon.awscdk.services.autoscaling.AutoScalingGroupProps
import software.amazon.awscdk.services.autoscaling.HealthCheck
import software.amazon.awscdk.services.autoscaling.IAutoScalingGroup
import software.amazon.awscdk.services.ec2.*
import software.amazon.awscdk.services.elasticloadbalancingv2.AddApplicationTargetsProps
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancer
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancerProps
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationProtocol
import software.amazon.awscdk.services.elasticloadbalancingv2.BaseApplicationListenerProps
import software.amazon.awscdk.services.iam.ManagedPolicy
import software.amazon.awscdk.services.iam.Role
import software.amazon.awscdk.services.iam.RoleProps
import software.amazon.awscdk.services.iam.ServicePrincipal
import software.constructs.Construct
import java.io.File

class ComputeStack(
    scope: Construct, id: String, props: StackProps
) : Stack(scope, id, props) {
    val asg: AutoScalingGroup

    init {
        // Get the default VPC. This is the network where your instance will be provisioned
        // All activated regions in AWS have a default vpc.
        // You can create your own of course as well. https://aws.amazon.com/vpc/
        val defaultVpc = Vpc.fromLookup(this, "VPC", VpcLookupOptions.builder().isDefault(true).build())

        // Lets create a role for the instance
        // You can attach permissions to a role and determine what your
        // instance can or can not do
        val role = Role(
            this,
            "simple-instance-1-role", // this is a unique id that will represent this resource in a Cloudformation template
            RoleProps.builder().assumedBy(ServicePrincipal("ec2.amazonaws.com"))
                .managedPolicies(listOf(
                    ManagedPolicy.fromAwsManagedPolicyName("service-role/AmazonEC2RoleforAWSCodeDeploy")
                ))
                .build()
        )

        val loadBalancer = ApplicationLoadBalancer(
            this, "simple-load-balancer", ApplicationLoadBalancerProps.builder()
                .vpc(defaultVpc)
                .internetFacing(true)
                .build()
        )

        val httpListener = loadBalancer.addListener(
            "ALBListenerHttp", BaseApplicationListenerProps.builder()
                .protocol(ApplicationProtocol.HTTP)
                .port(80)
                .build()
        )

        val userData = File("../scripts/ec2/startup.sh").readText()

        // Provision ASG for EC2 instances
        asg = AutoScalingGroup(
            this, "AutoScalingGroup", AutoScalingGroupProps.builder()
                .vpc(defaultVpc)
                .role(role)
                .instanceType(InstanceType.of(InstanceClass.T2, InstanceSize.MICRO))
                .machineImage(
                    MachineImage.latestAmazonLinux(
                        AmazonLinuxImageProps.builder().generation(AmazonLinuxGeneration.AMAZON_LINUX_2).build()
                    )
                )
                .userData(UserData.custom(userData))
                .healthCheck(HealthCheck.ec2())
                .minCapacity(1)
                .maxCapacity(2)
                .keyName("default-ssh-key")
                .build()
        )

        Tags.of(asg).add(
            CODE_DEPLOY_EC2_TAG.first, CODE_DEPLOY_EC2_TAG.second,
            TagProps.builder()
                .applyToLaunchedInstances(true)
                .build()
        )

        CfnOutput(
            this, "myAsgRef", CfnOutputProps.builder()
                .value(asg.autoScalingGroupName)
                .description("Name of ASG in stack")
                .exportName("myAsg")
                .build()
        )

        httpListener.addTargets(
            "TargetGroup", AddApplicationTargetsProps.builder()
                .port(80)
                .protocol(ApplicationProtocol.HTTP)
                .targets(listOf(asg))
                .healthCheck(
                    software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck.builder()
                        .port("80")
                        .path("/actuator/health")
                        .healthyHttpCodes("200")
                        .build()
                )
                .build()
        )
    }
}