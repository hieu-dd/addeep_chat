package net.itanchi.addeep.server.config.log.server

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator

class ServerLoggingDecorator(delegate: ServerWebExchange) : ServerWebExchangeDecorator(delegate) {
    override fun getRequest(): ServerHttpRequest = ServerRequestLoggingDecorator(delegate)
    override fun getResponse(): ServerHttpResponse = ServerResponseLoggingDecorator(delegate)
}