package wedding.backend.app.aws

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.changeMessageVisibility
import aws.sdk.kotlin.services.sqs.deleteMessage
import aws.sdk.kotlin.services.sqs.model.Message
import aws.sdk.kotlin.services.sqs.receiveMessage
import org.springframework.stereotype.Service

@Service
class SqsService(private val sqsClient: SqsClient) {

    suspend fun getMessages(sqsUrl: String, waitTimeoutSeconds: Int = 10, maxMessages: Int = 1, visibilityTimeout: Int = 10): List<Message> {
            val messages = sqsClient.receiveMessage {
            queueUrl = sqsUrl
            maxNumberOfMessages = maxMessages
            waitTimeSeconds = waitTimeoutSeconds
            this.visibilityTimeout = visibilityTimeout
        }

        return messages.messages ?: emptyList()
    }

    suspend fun deleteMessage(sqsUrl: String, message: Message) {
        sqsClient.deleteMessage {
            queueUrl = sqsUrl
            receiptHandle = message.receiptHandle
        }
    }

    suspend fun changeVisibility(sqsUrl: String, message: Message, timeout: Int) {
        sqsClient.changeMessageVisibility {
            queueUrl = sqsUrl
            receiptHandle = message.receiptHandle
            visibilityTimeout = timeout
        }
    }
}