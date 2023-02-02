package net.itanchi.addeep.server.filter.log

import net.itanchi.addeep.server.filter.BaseWebFilter
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import net.itanchi.addeep.server.config.log.server.ServerLoggingDecorator

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class LogFilter : BaseWebFilter() {
    private val logger = LoggerFactory.getLogger(LogFilter::class.java)

    override fun filterApi(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestPath = exchange.request.uri
        logger.debug("Logging server request at $requestPath with method: ${exchange.request.method}")
        return chain.filter(ServerLoggingDecorator(exchange))
            .doOnError {
                logger.error("Logging server response at $requestPath --> An error has occurred ", it)
            }
    }

}
