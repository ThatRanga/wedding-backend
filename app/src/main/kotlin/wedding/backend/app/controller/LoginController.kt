package wedding.backend.app.controller

import com.auth0.jwt.JWT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wedding.backend.app.model.LoginRequest
import wedding.backend.app.model.LoginResponse
import wedding.backend.app.model.VerifyRequest
import wedding.backend.app.model.VerifyResponse
import wedding.backend.app.services.JwtService

@RestController
@RequestMapping("/identity")
class LoginController(private val jwtService: JwtService) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        return try {
            val token = jwtService.create(loginRequest.username)
            ResponseEntity.ok(LoginResponse(status = "Authentication Successful", token = token))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(LoginResponse(status = "Error while logging in"))
        }
    }

    @PostMapping("/verify")
    fun verify(@RequestBody verifyRequest: VerifyRequest): ResponseEntity<VerifyResponse> {
        val jwt = JWT.decode(verifyRequest.token)

        return try {
            val validJwt = jwtService.verify(jwt)
            if (validJwt) {
                ResponseEntity.ok(VerifyResponse(true))
            } else {
                ResponseEntity.badRequest().body(VerifyResponse(false))
            }
        } catch (e: Exception) {
            println(e)
            return ResponseEntity.badRequest().body(VerifyResponse(false))
        }
    }
}