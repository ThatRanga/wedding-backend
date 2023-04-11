package wedding.backend.app.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "app.aws")
@Configuration
data class AwsProperties(var endpoint: String = "", var region: String = "")
