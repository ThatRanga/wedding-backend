package wedding.backend.infrastructure

import software.amazon.awscdk.services.ec2.IVpc
import software.amazon.awscdk.services.ec2.Vpc
import software.amazon.awscdk.services.ec2.VpcLookupOptions
import software.constructs.Construct

class WeddingBackendNetworkConstruct(scope: Construct): Construct(scope, "network") {
    val vpc: IVpc = Vpc.fromLookup(this, "vpc", VpcLookupOptions.builder().isDefault(true).build())
}