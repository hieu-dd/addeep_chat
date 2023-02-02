package net.itanchi.addeep.server.service.gif

import net.itanchi.addeep.server.exception.Error
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono

@Component
class GiphyApi(
    @Qualifier("giphyWebClient") private val webClient: WebClient,
) {
    companion object {
        const val GET_GIFS_END_POINT = "/v1/gifs/search"
        const val GET_TRENDING_GIFS_END_POINT = "/v1/gifs/trending"
    }

    suspend fun getGifs(
        limit: Int,
        offset: Int,
        filter: String
    ): GiphyGetGifsResponse = webClient.get()
        .uri { builder ->
            builder.path(GET_GIFS_END_POINT)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .queryParam("q", filter)
                .build()
        }
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) {
            Mono.just(Error.GiphyError.GetGifsGiphyFailed)
        }
        .onStatus(HttpStatus::is5xxServerError) {
            Mono.just(Error.GiphyError.ConnectGiphyFailed)
        }
        .awaitBody()

    suspend fun getTrendingGifs(
        limit: Int,
        offset: Int,
    ): GiphyGetGifsResponse = webClient.get()
        .uri { builder ->
            builder.path(GET_TRENDING_GIFS_END_POINT)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build()
        }
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) {
            Mono.just(Error.GiphyError.GetGifsGiphyFailed)
        }
        .onStatus(HttpStatus::is5xxServerError) {
            Mono.just(Error.GiphyError.ConnectGiphyFailed)
        }
        .awaitBody()
}