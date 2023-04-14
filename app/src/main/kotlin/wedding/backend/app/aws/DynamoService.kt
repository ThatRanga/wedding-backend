package wedding.backend.app.aws

import aws.sdk.kotlin.services.dynamodb.*
import aws.sdk.kotlin.services.dynamodb.model.*
import org.springframework.stereotype.Service
import wedding.backend.app.model.DynamoItem

@Service
class DynamoService(private val dynamoDb: DynamoDbClient) {

    suspend fun createTable(tableName: String, hashAttribute: String, rangeAttribute: String? = null) {
        val keySchema = mutableListOf(KeySchemaElement.invoke {
            keyType = KeyType.Hash
            attributeName = hashAttribute
        })
        val attributeDefinitions = mutableListOf(AttributeDefinition.invoke {
            attributeName = hashAttribute
            attributeType = ScalarAttributeType.S
        })

        if (rangeAttribute != null) {
            keySchema.add(KeySchemaElement.invoke {
                keyType = KeyType.Range
                attributeName = rangeAttribute
            })
            attributeDefinitions.add(AttributeDefinition.invoke {
                attributeName = rangeAttribute
                attributeType = ScalarAttributeType.S
            })
        }

        dynamoDb.createTable {
            this.tableName = tableName
            billingMode = BillingMode.Provisioned
            provisionedThroughput = ProvisionedThroughput.invoke {
                readCapacityUnits = 10
                writeCapacityUnits = 10
            }
            this.keySchema = keySchema
            this.attributeDefinitions = attributeDefinitions
        }
    }

    suspend fun tableExist(tableName: String): Boolean {
        return try {
            val response = dynamoDb.describeTable {
                this.tableName = tableName
            }
            response.table != null
        } catch (e: ResourceNotFoundException) {
            false
        }
    }

    suspend fun getItem(
        tableName: String,
        partitionKey: String,
        sortKey: String? = null
    ): Map<String, AttributeValue>? {
        val key = mutableMapOf("PK" to AttributeValue.S(partitionKey))
        if (sortKey != null) {
            key.plus("SK" to AttributeValue.S(sortKey))
        }

        return dynamoDb.getItem {
            this.tableName = tableName
            this.key = key
        }.item
    }

    suspend fun saveItem(tableName: String, item: DynamoItem) {
        dynamoDb.putItem {
            this.tableName = tableName
            this.item = item.toDynamoItem()
        }
    }
}