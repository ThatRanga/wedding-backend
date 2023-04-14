package wedding.backend.infrastructure

import software.amazon.awscdk.services.kms.Key
import software.amazon.awscdk.services.kms.KeyProps
import software.amazon.awscdk.services.kms.KeySpec
import software.amazon.awscdk.services.kms.KeyUsage
import software.constructs.Construct

class WeddingBackendKmsJwtConstruct(scope: Construct, env: String): Construct(scope, "${scope.node.id}-kms-jwt") {
    init {
        Key(
            this, "${this.node.id}-key", KeyProps.builder()
                .alias("${env}-authentication-key")
                .keyUsage(KeyUsage.SIGN_VERIFY)
                .keySpec(KeySpec.RSA_2048)
                .build()
        )
    }
}