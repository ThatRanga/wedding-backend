package wedding.backend.app.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import wedding.backend.app.properties.KmsProperties

@Configuration
class KmsConfiguration(kmsProperties: KmsProperties, environment: Environment) {
    final val authenticationAlias: String

    init {
        authenticationAlias = "alias/${environment.activeProfiles.first()}-${kmsProperties.authenticationAlias}"
    }
}