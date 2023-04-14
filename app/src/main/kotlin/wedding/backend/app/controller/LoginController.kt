package wedding.backend.app.controller

import com.auth0.jwt.JWT
import org.slf4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wedding.backend.app.model.LoginRequest
import wedding.backend.app.model.LoginResponse
import wedding.backend.app.model.VerifyRequest
import wedding.backend.app.model.VerifyResponse
import wedding.backend.app.security.DefaultUserDetailsService
import wedding.backend.app.services.JwtService

@RestController
@RequestMapping("/identity")
class LoginController(
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val logger: Logger
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password
                )
            )
            SecurityContextHolder.getContext().authentication = authentication

            val userDetails = authentication.principal as DefaultUserDetailsService.DefaultUserDetails

            val token = jwtService.create(userDetails.username)
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
            logger.error("Error while validating token", e)
            return ResponseEntity.badRequest().body(VerifyResponse(false))
        }
    }
}