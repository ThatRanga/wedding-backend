package wedding.backend.infrastructure

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.IVpc
import software.amazon.awscdk.services.ec2.Vpc
import software.amazon.awscdk.services.ec2.VpcLookupOptions
import software.constructs.Construct

class WeddingBackendNetworkStack(scope: Construct, id: String, stackProps: StackProps): Stack(scope, id, stackProps) {
    val vpc: IVpc = Vpc.fromLookup(this, "vpc", VpcLookupOptions.builder().isDefault(true).build())
}