package net.itanchi.addeep.server.config.log.client

import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.client.reactive.ClientHttpResponse
import org.springframework.http.client.reactive.ClientHttpResponseDecorator
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets

class ClientResponseLoggingDecorator(
    private val uri: URI,
    delegate: ClientHttpResponse,
) : ClientHttpResponseDecorator(delegate) {
    private val logger = LoggerFactory.getLogger(ClientResponseLoggingDecorator::class.java)

    override fun getBody(): Flux<DataBuffer> {
        val byteArrayOutputStream = ByteArrayOutputStream()
        return super.getBody()
            .publishOn(Schedulers.boundedElastic())
            .doOnNext { dataBuffer ->
                try {
                    Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
                } catch (e: IOException) {
                    logger.error("Logging client response at $uri --> Cannot read data buffer", e)
                }
            }
            .doOnComplete {
                byteArrayOutputStream.use {
                    val responseBody = String(it.toByteArray(), StandardCharsets.UTF_8)
                    logger.debug("Logging client response at $uri with body: $responseBody")
                }
            }
            .doOnError {
                byteArrayOutputStream.use {
                    logger.error("Logging client response at $uri --> Cannot get body from response", it)
                }
            }
    }

}
