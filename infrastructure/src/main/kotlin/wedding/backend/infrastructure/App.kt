package wedding.backend.infrastructure

import software.amazon.awscdk.App
import software.amazon.awscdk.Environment
import software.amazon.awscdk.StackProps

fun main() {
    val app = App()

    ComputeStack(
        app, "test-stack", StackProps.builder()
            .env(
                Environment.builder()
                    .account("781525612065")
                    .region("ap-southeast-2")
                    .build()
            )
            .build()
    )

    CodeDeployStack(
        app, "test-code-deploy", StackProps.builder()
            .env(
                Environment.builder()
                    .account("781525612065")
                    .region("ap-southeast-2")
                    .build()
            )
            .build()
    )


    app.synth()
}