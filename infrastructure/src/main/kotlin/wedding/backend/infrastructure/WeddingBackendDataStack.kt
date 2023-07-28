package wedding.backend.infrastructure

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.sqs.Queue
import software.constructs.Construct

class WeddingBackendDataStack(scope: Construct, id: String, env: String, props: StackProps) : Stack(scope, id, props) {
    val queue: Queue
    init {
        WeddingBackendKmsJwtConstruct(this, env)
        WeddingBackendDynamoConstruct(this, env)
        queue = WeddingBackendUserUploadSqsConstruct(this, env).queue
    }
}