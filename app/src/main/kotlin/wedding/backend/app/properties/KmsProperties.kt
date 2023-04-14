package wedding.backend.app.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties("app.kms")
@Configuration
data class KmsProperties(var authenticationAlias: String = "")