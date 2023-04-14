package wedding.backend.app.util

import kotlinx.coroutines.runBlocking
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import wedding.backend.app.aws.DynamoService
import wedding.backend.app.aws.KmsService
import wedding.backend.app.model.Role
import wedding.backend.app.model.User
import wedding.backend.app.properties.DynamoProperties
import wedding.backend.app.properties.KmsProperties
import wedding.backend.app.services.UserService

@Component
class StartupHelper(
    private val kmsService: KmsService,
    private val dynamoService: DynamoService,
    private val passwordEncoder: PasswordEncoder,
    private val dynamoProperties: DynamoProperties,
    private val kmsProperties: KmsProperties,
    private val userService: UserService
) {

    @EventListener(classes = [ContextRefreshedEvent::class], condition = "@environment.getProperty('app.bootstrap')")
    fun bootstrapApp(event: ContextRefreshedEvent) {
        runBlocking {
            setupKms()
            setupDynamo()
        }
    }

    private suspend fun setupKms() {
        if (!kmsService.aliasExist(kmsProperties.authenticationAlias)) {
            kmsService.createKeyWithAlias(kmsProperties.authenticationAlias)
        }
    }

    private suspend fun setupDynamo() {
        if (!dynamoService.tableExist(dynamoProperties.tableName)) {
            dynamoService.createTable(dynamoProperties.tableName, dynamoProperties.pkField)
            userService.addUser( User(
                    "admin@admin.com",
                    "admin@admin.com",
                    passwordEncoder.encode("admin"),
                    "The",
                    "Admin",
                    listOf(Role.ROLE_USER, Role.ROLE_ADMIN)
                )
            )
        }
    }

}