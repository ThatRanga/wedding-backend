package wedding.backend.app.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import wedding.backend.app.properties.DynamoProperties

@Configuration
class DynamoConfiguration(dynamoProperties: DynamoProperties, environment: Environment) {
    final val tableName: String
    final val pkField: String

    init {
        tableName = "${environment.activeProfiles.first()}-${dynamoProperties.tableName}"
        pkField = dynamoProperties.pkField
    }
}