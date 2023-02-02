package net.itanchi.addeep.server.config.log.client

import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.http.client.reactive.ClientHttpResponse
import reactor.core.publisher.Mono
import java.net.URI
import java.util.function.Function


class ClientLoggingConnector(private val delegate: ClientHttpConnector) : ClientHttpConnector {
    private val logger = LoggerFactory.getLogger(ClientLoggingConnector::class.java)

    override fun connect(
        method: HttpMethod,
        uri: URI,
        requestCallback: Function<in ClientHttpRequest, Mono<Void>>
    ): Mono<ClientHttpResponse> {
        return delegate.connect(
            method,
            uri
        ) { request ->
            logger.debug("Logging client request at $uri with method ${request.method}")
            requestCallback.apply(ClientRequestLoggingDecorator(uri, request))
        }.map { response ->
            logger.debug("Logging client response at $uri with status ${response.statusCode}")
            ClientResponseLoggingDecorator(uri, response)
        }
    }
}
