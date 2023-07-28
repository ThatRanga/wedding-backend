package wedding.backend.infrastructure

import software.amazon.awscdk.App
import software.amazon.awscdk.Environment
import software.amazon.awscdk.StackProps

fun main() {
    val app = App()

    val network = WeddingBackendNetworkStack(app, "prod-wedding-backend-network", getStackProps())

    val data = WeddingBackendDataStack(app, "prod-wedding-backend-data", "prod", getStackProps(terminationProtection = true))

    WeddingBackendServerStack(app, "prod-wedding-backend-server", "prod", network.vpc, data.queue, getStackProps())

    WeddingBackendUserUploadStack(app, "prod-wedding-user-upload", "prod", network.vpc, data.queue, getStackProps())

    app.synth()
}

fun getStackProps(terminationProtection: Boolean = false): StackProps = StackProps.builder()
    .env(
        Environment.builder()
            .account("781525612065")
            .region("ap-southeast-2")
            .build()
    )
    .terminationProtection(terminationProtection)
    .build()