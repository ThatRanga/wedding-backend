package wedding.backend.app.configuration

import org.springframework.context.annotation.Configuration
import wedding.backend.app.properties.SqsProperties
import wedding.backend.app.properties.WorkerProperties

@Configuration
class UserUploadWorkerConfiguration(sqsProperties: SqsProperties, workerProperties: WorkerProperties) {
    final val sqsUrl: String
    final val visibilityTimeout: Int
    final val waitTime: Int
    final val maxMessages: Int
    final val numWorkers: Int

    init {
        sqsUrl = sqsProperties.queueUrl
        visibilityTimeout = sqsProperties.visibilityTimeout
        waitTime = sqsProperties.waitTimeout
        maxMessages = sqsProperties.maxMessages
        numWorkers = workerProperties.numWorkers
    }
}