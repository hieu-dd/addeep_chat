package net.itanchi.addeep.server.config.log.server

import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets

class ServerRequestLoggingDecorator(
    delegate: ServerWebExchange,
) : ServerHttpRequestDecorator(delegate.request) {
    private val logger = LoggerFactory.getLogger(ServerRequestLoggingDecorator::class.java)
    private val requestPath = delegate.request.uri

    override fun getBody(): Flux<DataBuffer> {
        val byteArrayOutputStream = ByteArrayOutputStream()
        return super.getBody()
            .publishOn(Schedulers.boundedElastic())
            .doOnNext { dataBuffer ->
                try {
                    Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
                } catch (e: IOException) {
                    logger.error("Logging server request at $requestPath --> Cannot read data buffer", e)
                }
            }
            .doOnComplete {
                byteArrayOutputStream.use {
                    val requestBody = String(it.toByteArray(), StandardCharsets.UTF_8)
                    logger.debug("Logging server request at $requestPath with body: $requestBody")
                }
            }
            .doOnError {
                byteArrayOutputStream.use {
                    logger.error("Logging server request at $requestPath --> Cannot get body from request", it)
                }
            }
    }
}
