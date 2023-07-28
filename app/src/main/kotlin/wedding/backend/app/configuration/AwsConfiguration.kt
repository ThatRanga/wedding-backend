package wedding.backend.app.configuration

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.smithy.kotlin.runtime.net.Url
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import wedding.backend.app.properties.AwsProperties

@Configuration
class AwsConfiguration(private val awsProperties: AwsProperties) {

    @Bean
    fun kmsClient(): KmsClient = KmsClient {
        region = awsProperties.region
        if(awsProperties.endpoint.isNotEmpty()) endpointUrl = Url.parse(awsProperties.endpoint)
    }

    @Bean
    fun dynamoClient(): DynamoDbClient = DynamoDbClient {
        region = awsProperties.region
        if (awsProperties.endpoint.isNotBlank()) endpointUrl = Url.parse(awsProperties.endpoint)
    }

    @Bean
    fun sqsClient(): SqsClient = SqsClient {
        region = awsProperties.region
        if (awsProperties.endpoint.isNotBlank()) endpointUrl = Url.parse(awsProperties.endpoint)
    }
}