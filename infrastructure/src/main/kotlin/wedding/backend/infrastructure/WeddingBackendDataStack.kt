package wedding.backend.infrastructure

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.s3.Bucket
import software.constructs.Construct

class WeddingBackendDataStack(scope: Construct, id: String, env: String, props: StackProps) : Stack(scope, id, props) {
    init {
        WeddingBackendKmsJwtConstruct(this, env)
        WeddingBackendDynamoConstruct(this, env)
    }
}