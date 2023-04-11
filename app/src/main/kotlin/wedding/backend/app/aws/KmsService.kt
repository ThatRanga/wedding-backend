package wedding.backend.app.aws

import aws.sdk.kotlin.services.kms.*
import aws.sdk.kotlin.services.kms.model.*
import aws.smithy.kotlin.runtime.net.Url
import org.springframework.stereotype.Service
import wedding.backend.app.properties.AwsProperties
import java.util.*

@Service
class KmsService(private val awsProperties: AwsProperties) {
    private val kmsClient: KmsClient =
        KmsClient {
            region = awsProperties.region
            if(awsProperties.endpoint.isNotEmpty()) endpointUrl = Url.parse(awsProperties.endpoint)
        }

    private val base64UrlEncoder = Base64.getUrlEncoder().withoutPadding()

    suspend fun sign(headerBytes: ByteArray, payloadBytes: ByteArray, alias: String): String {
        val signKeyId = kmsClient.describeKey {
            keyId = "alias/${alias}"
        }.keyMetadata?.keyId

        val contentBytes = headerBytes + '.'.code.toByte() + payloadBytes
        val signResponse = kmsClient.sign {
            keyId = signKeyId ?: throw Error("Couldn't find key for alias 'alias/${alias}'")
            messageType = MessageType.Raw
            message = contentBytes
            signingAlgorithm = SigningAlgorithmSpec.RsassaPkcs1V1_5_Sha256
        }

        return base64UrlEncoder.encodeToString(signResponse.signature)
    }

    suspend fun verify(signableContent: ByteArray, signature: ByteArray, alias: String): VerifyResponse {
        val signKeyId = kmsClient.describeKey {
            keyId = "alias/${alias}"
        }.keyMetadata?.keyId

        return kmsClient.verify {
            keyId = signKeyId
            message = signableContent
            this.signature = signature
            this.signingAlgorithm = SigningAlgorithmSpec.RsassaPkcs1V1_5_Sha256
        }
    }

    suspend fun aliasExist(alias: String): Boolean {
        val aliases = kmsClient.listAliases()

        return aliases.aliases?.any { it.aliasName == "alias/${alias}" } ?: false
    }
    suspend fun createKeyWithAlias(alias: String) {
        val response = kmsClient.createKey {
            keyUsage = KeyUsageType.SignVerify
            keySpec = KeySpec.Rsa2048
        }

        if (response.keyMetadata == null || response.keyMetadata?.keyId == null) {
            throw Error("Key not created")
        }

        kmsClient.createAlias {
            targetKeyId = response.keyMetadata?.keyId
            aliasName = "alias/${alias}"
        }
    }
}