package wedding.backend.app.model

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class User(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val roles: List<Role>
) : DynamoItem(createPartitionKey(username)) {
    override fun getOtherAttributes(): Map<String, AttributeValue> = mapOf(
        FIRST_NAME_FIELD to AttributeValue.S(firstName),
        LAST_NAME_FIELD to AttributeValue.S(lastName),
        PASSWORD_FIELD to AttributeValue.S(password),
        EMAIL_FIELD to AttributeValue.S(email),
        ROLES_FIELD to AttributeValue.S(Json.encodeToString(roles))
    )

    companion object {
        private const val PARTITION_KEY_PREFIX = "user"
        private const val FIRST_NAME_FIELD = "firstName"
        private const val LAST_NAME_FIELD = "lastName"
        private const val PASSWORD_FIELD = "password"
        private const val EMAIL_FIELD = "email"
        private const val ROLES_FIELD = "roles"

        fun createPartitionKey(username: String): String = "${PARTITION_KEY_PREFIX}#${username}"

        fun convertFromDynamoItem(dynamoItem: Map<String, AttributeValue>): User {
            return User(
                dynamoItem.getValue("PK").asS(),
                dynamoItem.getValue(EMAIL_FIELD).asS(),
                dynamoItem.getValue(PASSWORD_FIELD).asS(),
                dynamoItem.getValue(FIRST_NAME_FIELD).asS(),
                dynamoItem.getValue(LAST_NAME_FIELD).asS(),
                dynamoItem.getValue(ROLES_FIELD).asS().let { Json.decodeFromString<List<Role>>(it) }
            )
        }

    }
}