package net.itanchi.addeep.core.data.api

import co.touchlab.stately.ensureNeverFrozen
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.itanchi.addeep.core.data.api.request.DownloadRequest
import net.itanchi.addeep.core.data.api.request.GifRequest
import net.itanchi.addeep.core.data.api.response.GifResponse

internal class MediaApiImpl(
    private val mediaClient: HttpClient
) : MediaApi {

    companion object {
        private const val API_GIF_PATH = "/api/v1/media/gifs"
    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun download(request: DownloadRequest): Flow<ByteArray> = flow {
        mediaClient.get<HttpStatement>("/api/v1/${request.key}").execute { response ->
            val channel: ByteReadChannel = response.receive()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    emit(packet.readBytes())
                }
            }
        }
    }

    override suspend fun getGifs(request: GifRequest): GifResponse {
        return mediaClient.get(API_GIF_PATH) {
            parameter("filter", request.filter)
            parameter("page", request.page)
            parameter("pageSize", request.pageSize)
        }
    }
}