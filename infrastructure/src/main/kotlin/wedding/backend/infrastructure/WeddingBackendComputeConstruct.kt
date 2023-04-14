package wedding.backend.infrastructure

import software.amazon.awscdk.services.autoscaling.AutoScalingGroup
import software.amazon.awscdk.services.autoscaling.AutoScalingGroupProps
import software.amazon.awscdk.services.autoscaling.HealthCheck
import software.amazon.awscdk.services.ec2.*
import software.amazon.awscdk.services.elasticloadbalancingv2.*
import software.amazon.awscdk.services.iam.ManagedPolicy
import software.amazon.awscdk.services.iam.Role
import software.amazon.awscdk.services.iam.RoleProps
import software.amazon.awscdk.services.iam.ServicePrincipal
import software.constructs.Construct
import java.io.File

class WeddingBackendComputeConstruct(scope: Construct, vpc: IVpc): Construct(scope, "${scope.node.id}-compute") {
    val asg: AutoScalingGroup
    init {
        val role = Role(
            this,
            "${this.node.id}-ec2-role", // this is a unique id that will represent this resource in a Cloudformation template
            RoleProps.builder().assumedBy(ServicePrincipal("ec2.amazonaws.com"))
                .managedPolicies(
                    listOf(
                        ManagedPolicy.fromAwsManagedPolicyName("service-role/AmazonEC2RoleforAWSCodeDeploy"),
                        ManagedPolicy.fromAwsManagedPolicyName("CloudWatchAgentServerPolicy"),
                        ManagedPolicy.fromAwsManagedPolicyName("service-role/AmazonEC2RoleforSSM")
                    )
                )
                .build()
        )

        val loadBalancer = ApplicationLoadBalancer(
            this, "${this.node.id}-alb", ApplicationLoadBalancerProps.builder()
                .vpc(vpc)
                .internetFacing(true)
                .build()
        )

        loadBalancer.addListener(
            "${loadBalancer.node.id}-listener-http", BaseApplicationListenerProps.builder()
                .protocol(ApplicationProtocol.HTTP)
                .port(80)
                .defaultAction(
                    ListenerAction.redirect(
                        RedirectOptions.builder().protocol("HTTPS").permanent(true).build()
                    )
                )
                .build()
        )

        val httpsListener = loadBalancer.addListener(
            "${loadBalancer.node.id}-listener-https", BaseApplicationListenerProps.builder()
                .protocol(ApplicationProtocol.HTTPS)
                .port(443)
                .certificates(listOf(ListenerCertificate.fromArn("arn:aws:acm:ap-southeast-2:781525612065:certificate/3bdbe752-88bb-4a3a-9393-0d9be15ff58b")))
                .build()
        )

        val userData = File("../scripts/ec2/startup.sh").readText()

        // Provision ASG for EC2 instances
        asg = AutoScalingGroup(
            this, "${this.node.id}-asg", AutoScalingGroupProps.builder()
                .vpc(vpc)
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
                .build()
        )

        val applicationTargetProps = AddApplicationTargetsProps.builder()
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

        httpsListener.addTargets("${httpsListener.node.id}-target-group", applicationTargetProps)
    }
}