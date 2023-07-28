package wedding.backend.app.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties("app.sqs")
@Configuration
data class SqsProperties(
    var queueUrl: String = "",
    var visibilityTimeout: Int = 5,
    var waitTimeout: Int = 5,
    var maxMessages: Int = 1
)