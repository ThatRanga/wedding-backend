package wedding.backend.infrastructure

import software.amazon.awscdk.App
import software.amazon.awscdk.Environment
import software.amazon.awscdk.StackProps

fun main() {
    val app = App()

    WeddingBackendServerStack(app, "prod-wedding-backend-server", "prod", StackProps.builder()
        .env(
            Environment.builder()
                .account("781525612065")
                .region("ap-southeast-2")
                .build()
        )
        .build()
    )

    WeddingBackendDataStack(app, "prod-wedding-backend-data", "prod", StackProps.builder()
        .env(
            Environment.builder()
                .account("781525612065")
                .region("ap-southeast-2")
                .build()
        )
        .terminationProtection(true)
        .build()
    )

    app.synth()
}