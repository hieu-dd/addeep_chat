package net.itanchi.addeep.server.config.log.client

import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.http.client.reactive.ClientHttpRequestDecorator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets

class ClientRequestLoggingDecorator(
    private val uri: URI,
    delegate: ClientHttpRequest,
) : ClientHttpRequestDecorator(delegate) {
    private val logger = LoggerFactory.getLogger(ClientRequestLoggingDecorator::class.java)

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        val byteArrayOutputStream = ByteArrayOutputStream()
        return super.writeWith(
            Flux.from(body)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext { dataBuffer ->
                    try {
                        Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
                    } catch (e: IOException) {
                        logger.error("Logging client request at $uri --> Cannot read data buffer", e)
                    }
                }
                .doOnComplete {
                    byteArrayOutputStream.use {
                        val requestBody = String(it.toByteArray(), StandardCharsets.UTF_8)
                        logger.debug("Logging client request at $uri with body: $requestBody")
                    }
                }.doOnError {
                    byteArrayOutputStream.use {
                        logger.error("Logging client request at $uri --> Cannot get body from request", it)
                    }
                }
        )
    }

}
