package wedding.backend.app.services

import com.auth0.jwt.HeaderParams
import com.auth0.jwt.RegisteredClaims
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import wedding.backend.app.aws.KmsService
import java.nio.charset.StandardCharsets
import java.util.*

@Service
class JwtService(private val kmsService: KmsService, private val objectMapper: ObjectMapper) {
    private val base64UrlEncoder = Base64.getUrlEncoder().withoutPadding()
    private val base64UrlDecoder = Base64.getUrlDecoder()

    fun create(username: String): String {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val expiryDate = calendar.add(Calendar.HOUR, 1).let { calendar.time }


        val payloadClaims = mapOf(
            RegisteredClaims.SUBJECT to username,
            RegisteredClaims.EXPIRES_AT to expiryDate.time,
            RegisteredClaims.ISSUED_AT to currentTime.time,
            RegisteredClaims.NOT_BEFORE to currentTime.time,
            RegisteredClaims.ISSUER to "luke.daisy.wedding",
            RegisteredClaims.JWT_ID to UUID.randomUUID().toString(),
        )

        val headerClaims = mapOf(
            HeaderParams.TYPE to "JWT",
            HeaderParams.ALGORITHM to "RS256"
        )

        return sign(payloadClaims, headerClaims)
    }

    fun verify(jwt: DecodedJWT): Boolean {
        val signableContent = "${jwt.header}.${jwt.payload}".toByteArray(StandardCharsets.UTF_8)
        val signature = base64UrlDecoder.decode(jwt.signature)

        val result = runBlocking {
            kmsService.verify(signableContent, signature, "authentication-key")
        }

        return result.signatureValid
    }

    private fun sign(payloadClaims: Map<String, Any>, headerClaims: Map<String, String>): String {
        val headerJson = objectMapper.writeValueAsString(headerClaims)
        val payloadJson = objectMapper.writeValueAsString(payloadClaims)

        val header = encodeBase64UrlSafe(headerJson)
        val payload = encodeBase64UrlSafe(payloadJson)

        val signature = runBlocking {
            kmsService.sign(
                header.toByteArray(StandardCharsets.UTF_8),
                payload.toByteArray(StandardCharsets.UTF_8),
                "authentication-key"
            )
        }

        return "$header.$payload.$signature"
    }

    private fun encodeBase64UrlSafe(payload: String): String =
        base64UrlEncoder.encodeToString(payload.toByteArray(StandardCharsets.UTF_8))

}