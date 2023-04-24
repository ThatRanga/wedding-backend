package wedding.backend.userlambda

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.smithy.kotlin.runtime.content.toByteArray
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream


class UserLambdaHandler : RequestHandler<S3Event, Unit> {

    override fun handleRequest(input: S3Event?, context: Context?) {
        val s3Client = S3Client { region = REGION }
        val sqsClient = SqsClient { region = REGION }

        if (input == null) {
            throw Error("No input provided to function")
        }

        val s3Record = input.records.first().s3
        val bucketName = s3Record.bucket.name
        val fileName = s3Record.`object`.key

        val fileReader = runBlocking {
            s3Client.getObject(GetObjectRequest.invoke {
                this.bucket = bucketName
                this.key = fileName
            }) { resp ->
                ByteArrayInputStream(resp.body?.toByteArray() ?: throw Error("Couldn't read body")).bufferedReader()
            }
        }

        fileReader.readLine() //headers

        fileReader.lineSequence().forEach { line ->
            val (username, password) = line.split(",", limit = 2)
            println(username)
        }
    }

    data class UserDetails(val username: String, val password: String)

    companion object {
        const val REGION = "ap-southeast-2"
    }
}
