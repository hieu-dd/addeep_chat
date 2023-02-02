package net.itanchi.addeep.server.rest

import kotlinx.serialization.SerializationException
import net.itanchi.addeep.server.exception.Error
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ResponseStatusException

@Component
@Order(-2)
class ResponseErrorHandler(
    attributes: ErrorAttributes,
    webProperties: WebProperties,
    context: ApplicationContext,
    configurer: ServerCodecConfigurer,
) : AbstractErrorWebExceptionHandler(attributes, webProperties.resources, context) {
    companion object {
        const val WWW_AUTHENTICATE = "WWW-Authenticate";
        const val DEFAULT_REALM = "Realm";
        const val WWW_AUTHENTICATE_FORMAT = "Bearer realm=\"%s\"";
    }

    init {
        super.setMessageReaders(configurer.readers)
        super.setMessageWriters(configurer.writers)
    }

    override fun getRoutingFunction(attributes: ErrorAttributes) = coRouter {
        RequestPredicates.all().invoke { request ->
            val error = when (val requestError = attributes.getError(request)) {
                is SerializationException -> Error.SerializationError(
                    message = requestError.message.orEmpty(),
                    cause = requestError,
                )
                is ResponseStatusException -> when (requestError.status) {
                    HttpStatus.NOT_FOUND -> Error.NotFoundError(requestError)
                    else -> Error.UnexpectedError(requestError)
                }
                is Error -> requestError
                else -> Error.UnexpectedError(cause = requestError)
            }
            val httpStatus = HttpStatus.valueOf(error.code / 1000)
            ServerResponse.status(httpStatus)
                .headers {
                    if (httpStatus == HttpStatus.UNAUTHORIZED)
                        it.set(WWW_AUTHENTICATE, createWWAuthenticateHeaderValue(DEFAULT_REALM))
                }
                .json()
                .bodyValueAndAwait(
                    ErrorResponse(
                        code = error.code,
                        message = error.message
                    )
                )
        }
    }

    private fun createWWAuthenticateHeaderValue(realm: String): String {
        return String.format(WWW_AUTHENTICATE_FORMAT, realm)
    }
}