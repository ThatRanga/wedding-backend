package wedding.backend.app.model

import kotlinx.serialization.Serializable
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Serializable
enum class Role {
    ROLE_ADMIN,
    ROLE_USER;

    fun toGrantedAuthority(): GrantedAuthority = SimpleGrantedAuthority(this.toString())
}