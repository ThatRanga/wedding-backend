package wedding.backend.app.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties("app.dynamo")
@Configuration
data class DynamoProperties(var tableName: String = "", var pkField: String = "")