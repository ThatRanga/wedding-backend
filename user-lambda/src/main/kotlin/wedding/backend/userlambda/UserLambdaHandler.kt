package wedding.backend.userlambda

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.SendMessageBatchRequest
import aws.sdk.kotlin.services.sqs.model.SendMessageBatchRequestEntry
import aws.smithy.kotlin.runtime.content.toByteArray
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream


class UserLambdaHandler : RequestHandler<S3Event, Unit> {
    private val gson = Gson()
    private val s3Client = S3Client { region = REGION }
    private val sqsClient = SqsClient { region = REGION }

    override fun handleRequest(input: S3Event?, context: Context?) {
        val logger = context?.logger

        if (input == null) {
            throw Error("No input provided to function")
        }

        val s3Record = input.records.first().s3
        val bucketName = s3Record.bucket.name
        val fileName = s3Record.`object`.key

        logger?.log("Bucket Name: ${bucketName}, File Name: ${fileName}")

        val userDetails = getDetailsFromFile(bucketName, fileName)

        sendDetailsToQueue(userDetails)
    }

    private fun getDetailsFromFile(bucketName: String, key: String): List<UserDetails> {
        val fileReader = runBlocking {
            s3Client.getObject(GetObjectRequest.invoke {
                this.bucket = bucketName
                this.key = key
            }) { resp ->
                ByteArrayInputStream(resp.body?.toByteArray() ?: throw Error("Couldn't read body")).bufferedReader()
            }
        }

        fileReader.readLine() //headers

        return fileReader.lineSequence().map { line ->
            val (username, password) = line.split(",", limit = 2)
            UserDetails(username, password)
        }.toList()
    }

    private fun sendDetailsToQueue(userDetails: List<UserDetails>) {
        userDetails.chunked(10).forEach { userDetailsBlock ->
            runBlocking {
                sqsClient.sendMessageBatch(SendMessageBatchRequest.invoke {
                    this.queueUrl = SQS_QUEUE_URL
                    this.entries = userDetailsBlock.map { userDetail ->
                        SendMessageBatchRequestEntry.invoke {
                            this.id = userDetail.username
                            this.messageGroupId = userDetail.username
                            this.messageBody = gson.toJson(userDetail)
                        }
                    }.toList()
                })
            }
        }
    }

    data class UserDetails(val username: String, val password: String)

    companion object {
        const val REGION = "ap-southeast-2"
        const val SQS_QUEUE_URL = "https://sqs.ap-southeast-2.amazonaws.com/781525612065/prod-user-queue"
    }
}
