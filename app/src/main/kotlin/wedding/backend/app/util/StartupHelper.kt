package wedding.backend.app.util

import kotlinx.coroutines.runBlocking
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import wedding.backend.app.aws.KmsService

@Component
class StartupHelper(private val kmsService: KmsService) {

    @EventListener(classes = [ContextRefreshedEvent::class], condition = "@environment.getProperty('app.bootstrap')")
    fun bootstrapApp(event: ContextRefreshedEvent) {
            runBlocking {
                setupKms()
        }
    }

    private suspend fun setupKms() {
        if (!kmsService.aliasExist("authentication-key")) {
            kmsService.createKeyWithAlias("authentication-key")
        }
    }

}