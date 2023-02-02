package net.itanchi.addeep.server.auth

import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Component
class ServerHttpBearerAuthenticationConverter(
    private val jwtCustomVerifier: JWTCustomVerifier
) : ServerAuthenticationConverter {
    companion object {
        const val BEARER = "Bearer "
    }

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return Mono.justOrEmpty(exchange)
            .flatMap { Mono.justOrEmpty(it.request.headers.getFirst(HttpHeaders.AUTHORIZATION)) }
            .filter { authValue -> authValue.length > BEARER.length }
            .flatMap { authValue -> Mono.justOrEmpty(authValue.substring(BEARER.length)) }
            .flatMap { jwtCustomVerifier.check(it) }
    }
}