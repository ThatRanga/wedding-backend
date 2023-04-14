package wedding.backend.app.services

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import wedding.backend.app.aws.DynamoService
import wedding.backend.app.configuration.DynamoConfiguration
import wedding.backend.app.model.User

@Service
class UserService(private val dynamoService: DynamoService, private val dynamoConfiguration: DynamoConfiguration) {

    fun getUser(username: String): User {
        val dynamoItem = runBlocking {
            dynamoService.getItem(
                dynamoConfiguration.tableName,
                User.createPartitionKey(username)
            )
        }

        return User.convertFromDynamoItem(dynamoItem ?: throw Error("Couldn't find user"))
    }

    fun addUser(user: User) {
        runBlocking {
            dynamoService.saveItem(dynamoConfiguration.tableName, user)
        }
    }
}