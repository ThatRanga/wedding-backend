package wedding.backend.app.util

import kotlinx.coroutines.runBlocking
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import wedding.backend.app.aws.DynamoService
import wedding.backend.app.aws.KmsService
import wedding.backend.app.aws.SqsService
import wedding.backend.app.configuration.DynamoConfiguration
import wedding.backend.app.configuration.KmsConfiguration
import wedding.backend.app.model.Role
import wedding.backend.app.services.UserService

@Component
class LocalStartupHelper(
    private val kmsService: KmsService,
    private val dynamoService: DynamoService,
    private val dynamoConfiguration: DynamoConfiguration,
    private val kmsConfiguration: KmsConfiguration,
    private val userService: UserService,
    private val sqsService: SqsService
) {

    @EventListener(classes = [ContextRefreshedEvent::class], condition = "@environment.getProperty('app.bootstrap')")
    @Order(1)
    fun bootstrapApp(event: ContextRefreshedEvent) {
        runBlocking {
            setupKms()
            setupDynamo()
            setupSqs()
        }
    }

    private suspend fun setupKms() {
        if (!kmsService.aliasExist(kmsConfiguration.authenticationAlias)) {
            kmsService.createKeyWithAlias(kmsConfiguration.authenticationAlias)
        }
    }

    private suspend fun setupDynamo() {
        if (!dynamoService.tableExist(dynamoConfiguration.tableName)) {
            dynamoService.createTable(dynamoConfiguration.tableName, dynamoConfiguration.pkField)
            userService.addUser(
                "admin@admin.com",
                "admin@admin.com",
               "admin",
                "The",
                "Admin",
                listOf(Role.ROLE_USER, Role.ROLE_ADMIN)

            )
        }
    }

    private suspend fun setupSqs() {
        if (!sqsService.queueExists("test-queue")) {
            sqsService.createQueue("test-queue")
        }
    }

}