package wedding.backend.app.security

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class DefaultUserDetailsService: UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw UsernameNotFoundException("User not found")
        }

        return DefaultUserDetails(1L, username, "testEmail", "1234", listOf(SimpleGrantedAuthority("ROLE_ADMIN")))
    }

    class DefaultUserDetails(
        private val id: Long,
        private val username: String,
        private val email: String,
        @JsonIgnore private val password: String,
        private val authorities: Collection<GrantedAuthority>
    ): UserDetails {
        override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities.toMutableList()

        override fun getPassword(): String = password
        override fun getUsername(): String = username

        override fun isAccountNonExpired(): Boolean = true

        override fun isAccountNonLocked(): Boolean = true

        override fun isCredentialsNonExpired(): Boolean = true

        override fun isEnabled(): Boolean = true

        override fun equals(other: Any?): Boolean {
            return if (this === other) {
                true
            } else if (other == null || this::class != other::class) {
                false
            } else {
                val user: DefaultUserDetails = other as DefaultUserDetails
                this.id == user.id
            }
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + username.hashCode()
            result = 31 * result + email.hashCode()
            result = 31 * result + authorities.hashCode()
            return result
        }
    }
}