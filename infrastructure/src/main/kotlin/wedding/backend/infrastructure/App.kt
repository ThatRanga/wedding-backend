package wedding.backend.infrastructure

import software.amazon.awscdk.App
import software.amazon.awscdk.Environment
import software.amazon.awscdk.StackProps

fun main() {
    val app = App()
    val stackPropsBuilder = StackProps.builder()
        .env(
            Environment.builder()
                .account("781525612065")
                .region("ap-southeast-2")
                .build()
        )

    val network = WeddingBackendNetworkStack(app, "prod-wedding-backend-network", stackPropsBuilder.build())

    WeddingBackendDataStack(
        app, "prod-wedding-backend-data", "prod", stackPropsBuilder
            .terminationProtection(true)
            .build()
    )

    WeddingBackendServerStack(app, "prod-wedding-backend-server", "prod", network.vpc, stackPropsBuilder.build())

    WeddingBackendUserUploadStack(app, "prod-wedding-user-upload", "prod", network.vpc, stackPropsBuilder.build())

    app.synth()
}