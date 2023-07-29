package wedding.backend.app.aws

import aws.sdk.kotlin.services.sqs.*
import aws.sdk.kotlin.services.sqs.model.Message
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

    suspend fun createQueue(queueName: String) {
        val queue = sqsClient.createQueue {
            this.queueName = queueName
        }
        println(queue.queueUrl)
    }

    suspend fun queueExists(queueName: String): Boolean {
        val queues = sqsClient.listQueues {
            this.queueNamePrefix = queueName
        }

        return queues.queueUrls !== null && queues.queueUrls?.isNotEmpty() == true
    }
}