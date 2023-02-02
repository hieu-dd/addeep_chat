package net.itanchi.addeep.server.auth

import kotlinx.coroutines.reactor.mono
import net.itanchi.addeep.server.service.user.UserService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JWTCustomVerifier(
    private val userService: UserService,
    private val authenticationService: AuthenticationServiceInterface
) {
    fun check(token: String): Mono<Authentication> = mono {
        try {
            val decodedToken = authenticationService.decodeToken(token) ?: return@mono null
            val phoneNumber = decodedToken.claims[authenticationService.getPhoneNumberClaimsKey()] as String
            val userId = userService.getOrCreateUser(
                firebaseUid = decodedToken.uid,
                name = decodedToken.name,
                phone = phoneNumber,
                email = decodedToken.email
            ).id
            UsernamePasswordAuthenticationToken(userId.toString(), null, listOf())
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }
}