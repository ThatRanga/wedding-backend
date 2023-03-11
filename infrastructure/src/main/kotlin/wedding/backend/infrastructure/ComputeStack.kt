package wedding.backend.infrastructure

import software.amazon.awscdk.CfnOutput
import software.amazon.awscdk.CfnOutputProps
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.Stack
import software.amazon.awscdk.services.ec2.*
import software.amazon.awscdk.services.iam.Role
import software.amazon.awscdk.services.iam.RoleProps
import software.amazon.awscdk.services.iam.ServicePrincipal
import software.constructs.Construct

class ComputeStack(
    scope: Construct, id: String, props: StackProps
) : Stack(scope, id, props) {
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
            RoleProps.builder().assumedBy(ServicePrincipal("ec2.amazonaws.com")).build()
        )

        // lets create a security group for our instance
        // A security group acts as a virtual firewall for your instance to control inbound and outbound traffic.
        val securityGroup = SecurityGroup(
            this,
            "simple-instance-1-sg",
            SecurityGroupProps.builder()
                .vpc(defaultVpc)
                .allowAllOutbound(true) // will let your instance send outboud traffic
                .securityGroupName("simple-instance-1-sg")
                .build()
        )

        // lets use the security group to allow inbound traffic on specific ports
        securityGroup.addIngressRule(
            Peer.anyIpv4(),
            Port.tcp(22),
            "Allows SSH access from Internet"
        )

        securityGroup.addIngressRule(
            Peer.anyIpv4(),
            Port.tcp(80),
            "Allows HTTP access from Internet"
        )

        securityGroup.addIngressRule(
            Peer.anyIpv4(),
            Port.tcp(443),
            "Allows HTTPS access from Internet"
        )

        // Finally lets provision our ec2 instance
        val instance = Instance(
            this, "simple-instance-1", InstanceProps.builder()
                .vpc(defaultVpc)
                .role(role)
                .securityGroup(securityGroup)
                .instanceName("simple-instance-1")
                .instanceType(InstanceType.of(InstanceClass.T2, InstanceSize.MICRO))
                .machineImage(MachineImage.latestAmazonLinux())
//                        .keyName("simple-instance-1-key")
                .build()
        )

        // cdk lets us output prperties of the resources we create after they are created
        // we want the ip address of this new instance so we can ssh into it later
        CfnOutput(this, "simple-instance-1-output", CfnOutputProps.builder().value(instance.instancePublicIp).build())
    }
}