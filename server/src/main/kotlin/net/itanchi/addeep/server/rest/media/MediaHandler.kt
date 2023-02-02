package net.itanchi.addeep.server.rest.media

import net.itanchi.addeep.server.rest.CustomHeaders
import net.itanchi.addeep.server.rest.media.response.GetGifsResponse
import net.itanchi.addeep.server.service.gif.GifService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class MediaHandler(
    private val gifService: GifService,
) {
    companion object {
        const val PAGE_QUERY_KEY = "page"
        const val PAGE_SIZE_QUERY_KEY = "pageSize"
        const val FILTER_QUERY_KEY = "filter"
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 20
    }

    suspend fun getGifs(request: ServerRequest): ServerResponse {
        val pageSize =
            request.queryParamOrNull(PAGE_SIZE_QUERY_KEY)?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE_SIZE
        val page = request.queryParamOrNull(PAGE_QUERY_KEY)?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE
        val filter = request.queryParamOrNull(FILTER_QUERY_KEY).orEmpty()
        val gifs = gifService.getGifs(
            page = page,
            pageSize = pageSize,
            filter = filter,
        )
        return ServerResponse.ok().json()
            .bodyValueAndAwait(
                GetGifsResponse(
                    data = gifs,
                )
            )
    }

    suspend fun downloadGif(request: ServerRequest): ServerResponse {
        val gifName = request.pathVariable("gifName")
        val resourceInfo = gifService.downloadGif(gifName)
        return ServerResponse.ok()
            .header(HttpHeaders.CONTENT_TYPE, resourceInfo.contentType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${gifName}")
            .header(CustomHeaders.DOWNLOAD_FILE_HEADER, gifName)
            .bodyValueAndAwait(
                InputStreamResource(resourceInfo.data)
            )
    }
}