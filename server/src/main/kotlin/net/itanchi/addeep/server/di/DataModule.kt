package net.itanchi.addeep.server.di

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import kotlinx.coroutines.reactor.mono
import net.itanchi.addeep.server.config.log.client.ClientLoggingConnector
import net.itanchi.addeep.server.config.service.Services
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import net.itanchi.addeep.server.config.service.Services.ServiceProperty.ServiceID.*
import net.itanchi.addeep.server.config.useKotlinSerialization
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.util.UriComponentsBuilder
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit

@Configuration
class DataModule {
    companion object {
        const val MAX_IN_MEMORY_SIZE = 1000000
        const val CONFIG_API_KEY = "apiKey"
        const val GIPHY_API_KEY_PARAM = "api_key"
    }

    @Bean
    fun giphyWebClient(
        webClientBuilder: WebClient.Builder,
        services: Services,
    ): WebClient {
        val serviceProperties = services.properties.first { it.id == GIPHY }
        val filter = ExchangeFilterFunction.ofRequestProcessor { request ->
            mono {
                val params = serviceProperties.configs.mapNotNull { (key, value) ->
                    when (key) {
                        CONFIG_API_KEY -> Pair(GIPHY_API_KEY_PARAM, value)
                        else -> null
                    }
                }.toMap()
                newRequest(
                    request = request,
                    params = params,
                )
            }
        }
        return buildWebClient(webClientBuilder, serviceProperties, listOf(filter))
    }

    private fun newRequest(
        request: ClientRequest,
        params: Map<String, String> = mapOf(),
        headers: Map<String, String> = mapOf(),
    ): ClientRequest {
        val newUri = UriComponentsBuilder.fromUri(request.url())
            .apply { params.forEach { (key, value) -> queryParam(key, value) } }
            .build()
            .toUri()
        return ClientRequest.from(request)
            .apply { headers.forEach { (key, value) -> header(key, value) } }
            .url(newUri)
            .build()
    }

    private fun buildWebClient(
        webClientBuilder: WebClient.Builder,
        properties: Services.ServiceProperty,
        filters: List<ExchangeFilterFunction> = listOf(),
    ): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.timeoutMillis.toInt())
            .doOnConnected { connection ->
                connection.addHandler(ReadTimeoutHandler(properties.timeoutMillis, TimeUnit.MILLISECONDS))
                connection.addHandler(WriteTimeoutHandler(properties.timeoutMillis, TimeUnit.MILLISECONDS))
            }

        return webClientBuilder
            .baseUrl(properties.baseUrl)
            .clientConnector(ClientLoggingConnector(ReactorClientHttpConnector(httpClient)))
            .exchangeStrategies(ExchangeStrategies.empty().codecs { clientCodecConfigurer ->
                clientCodecConfigurer.useKotlinSerialization()
                clientCodecConfigurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE)
            }.build())
            .filters { it.addAll(filters) }
            .build()
    }
}
