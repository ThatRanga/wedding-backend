package wedding.backend.app.model

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue

abstract class DynamoItem(private val partitionKey: String, private val sortKey: String? = null) {

    abstract fun getOtherAttributes(): Map<String, AttributeValue>

    fun toDynamoItem(): Map<String, AttributeValue> {
        val item = mutableMapOf(
            "PK" to AttributeValue.S(partitionKey),
        ).plus(getOtherAttributes())

        if (sortKey != null) item.plus("SK" to sortKey)

        return item
    }
}