package wedding.backend.app.util

import com.auth0.jwt.RegisteredClaims
import com.auth0.jwt.interfaces.DecodedJWT

fun DecodedJWT.getUsername(): String = this.getClaim(RegisteredClaims.SUBJECT).asString()