package wedding.backend.app.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties("app.worker")
@Configuration
data class WorkerProperties(var numWorkers: Int = 1) {
}