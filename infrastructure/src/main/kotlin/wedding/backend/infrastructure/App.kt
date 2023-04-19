package wedding.backend.infrastructure

import software.amazon.awscdk.App
import software.amazon.awscdk.Environment
import software.amazon.awscdk.StackProps

fun main() {
    val app = App()

    val dataStack = WeddingBackendDataStack(app, "prod-wedding-backend-data", "prod", StackProps.builder()
        .env(
            Environment.builder()
                .account("781525612065")
                .region("ap-southeast-2")
                .build()
        )
        .terminationProtection(true)
        .build()
    )

    val serverStack = WeddingBackendServerStack(app, "prod-wedding-backend-server", "prod", dataStack.bucket, StackProps.builder()
        .env(
            Environment.builder()
                .account("781525612065")
                .region("ap-southeast-2")
                .build()
        )
        .build()
    )

    serverStack.addDependency(dataStack)

    app.synth()
}