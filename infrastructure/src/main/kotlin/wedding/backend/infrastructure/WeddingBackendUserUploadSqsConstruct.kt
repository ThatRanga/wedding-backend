package wedding.backend.infrastructure

import software.amazon.awscdk.services.sqs.Queue
import software.amazon.awscdk.services.sqs.QueueProps
import software.constructs.Construct

class WeddingBackendUserUploadSqsConstruct(scope: Construct, env: String) : Construct(scope, "sqs") {
    val queue: Queue;
    init {
        queue = Queue(scope, "user", QueueProps.builder()
            .queueName("${env}-user-queue")
            .build()
        )
    }
}