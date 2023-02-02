package net.itanchi.addeep.server.auth

import com.auth0.jwt.exceptions.JWTVerificationException
import net.itanchi.addeep.server.exception.Error
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

class BearerTokenReactiveAuthenticationManager : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
    }
}

@Component
class RsocketBearerTokenReactiveAuthenticationManager(
    private val jwtCustomVerifier: JWTCustomVerifier
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return try {
            return jwtCustomVerifier.check((authentication as BearerTokenAuthenticationToken).token)
        } catch (e: JWTVerificationException) {
            Mono.error(Error.UnauthorizedError)
        }
    }
}