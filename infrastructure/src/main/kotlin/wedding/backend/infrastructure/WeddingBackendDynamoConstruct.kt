package wedding.backend.infrastructure

import software.amazon.awscdk.services.dynamodb.*
import software.constructs.Construct

class WeddingBackendDynamoConstruct(scope: Construct, env: String): Construct(scope, "${scope.node.id}-dynamo") {
    init {
        Table(
            this, "${this.node.id}-table", TableProps.builder()
                .tableName("${env}-wedding-data")
                .partitionKey(Attribute.builder().name("PK").type(AttributeType.STRING).build())
                .billingMode(BillingMode.PROVISIONED)
                .readCapacity(10)
                .writeCapacity(10)
                .build()
        )
    }
}