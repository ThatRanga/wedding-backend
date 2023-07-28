package wedding.backend.app.util

import com.auth0.jwt.RegisteredClaims
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield

fun DecodedJWT.getUsername(): String = this.getClaim(RegisteredClaims.SUBJECT).asString()

suspend fun CoroutineScope.repeatUntilCancelled(block: suspend () -> Unit) {
    while (isActive) {
        try {
            block()
            yield()
        } catch (ex: CancellationException) {
            println("coroutine on thread ${Thread.currentThread().name} cancelled")
        } catch (ex: Exception) {
            println("coroutine on thread ${Thread.currentThread().name} failed with '${ex.message}'. Retrying...")
            ex.printStackTrace()
        }
    }
}