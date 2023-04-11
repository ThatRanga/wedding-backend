package wedding.backend.app.model

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(val token: String? = null, val status: String)

data class VerifyRequest(val token: String)

data class VerifyResponse(val verified: Boolean)