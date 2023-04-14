package wedding.backend.app.security

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import wedding.backend.app.services.JwtService
import wedding.backend.app.util.getUsername

class AuthTokenFilter(
    private val jwtService: JwtService,
    private val userDetailsService: DefaultUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt: String? = parseJwt(request)

            if (jwt != null) {
                val decodedJwt: DecodedJWT = JWT.decode(jwt)
                if (jwtService.verify(decodedJwt)) {
                    val userDetails = userDetailsService.loadUserByUsername(decodedJwt.getUsername())
                    val authentication =
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (e: Exception) {
            logger.error("Error while extracting auth token", e)
        }

        filterChain.doFilter(request, response)
    }

    private fun parseJwt(request: HttpServletRequest): String? {
        val headerAuth: String? = request.getHeader("Authorization")

        return if (!headerAuth.isNullOrBlank() && headerAuth.startsWith(BEARER_PREFIX)) {
            headerAuth.substring(BEARER_PREFIX.length)
        } else {
            null
        }

    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}