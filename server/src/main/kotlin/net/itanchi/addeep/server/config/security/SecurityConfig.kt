package net.itanchi.addeep.server.config.security

import net.itanchi.addeep.server.auth.BearerTokenReactiveAuthenticationManager
import net.itanchi.addeep.server.auth.RsocketBearerTokenReactiveAuthenticationManager
import net.itanchi.addeep.server.auth.ServerHttpBearerAuthenticationConverter
import net.itanchi.addeep.server.exception.Error
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono


@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        @Qualifier("bearerAuthenticationFilter") bearerAuthenticationFilter: AuthenticationWebFilter,
    ): SecurityWebFilterChain {
        http.csrf().disable()
            .authorizeExchange()
            .pathMatchers("/api/v1/**")
            .authenticated()
            .and()
            .addFilterAt(bearerAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .exceptionHandling()
            .authenticationEntryPoint { _, ex ->
                val error = when (ex) {
                    is AuthenticationCredentialsNotFoundException -> Error.UnauthorizedError
                    else -> Error.UnauthorizedError
                }
                Mono.error(error)
            }
        return http.build()
    }

    @Bean
    fun bearerAuthenticationFilter(
        passwordEncoder: PasswordEncoder,
        bearerConverter: ServerHttpBearerAuthenticationConverter
    ): AuthenticationWebFilter {

        val authManager = BearerTokenReactiveAuthenticationManager()
        val bearerAuthenticationFilter = AuthenticationWebFilter(authManager)

        bearerAuthenticationFilter.setServerAuthenticationConverter(bearerConverter)
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/v1/**"))
        return bearerAuthenticationFilter
    }

    @Bean
    fun payloadSocketAcceptorInterceptor(
        security: RSocketSecurity,
        rsocketBearerTokenReactiveAuthenticationManager: RsocketBearerTokenReactiveAuthenticationManager
    ): PayloadSocketAcceptorInterceptor {
        security.authorizePayload { authorize: RSocketSecurity.AuthorizePayloadsSpec ->
            authorize
                .setup()
                .permitAll()
                .anyRequest()
                .authenticated()
        } // all connections, exchanges.
            .jwt { jwtSpec ->
                try {
                    jwtSpec.authenticationManager(rsocketBearerTokenReactiveAuthenticationManager)
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        return security.build()
    }
}