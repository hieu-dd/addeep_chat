package net.itanchi.addeep.core.data.api

import kotlinx.coroutines.flow.Flow
import net.itanchi.addeep.core.data.api.request.DownloadRequest
import net.itanchi.addeep.core.data.api.request.GifRequest
import net.itanchi.addeep.core.data.api.response.GifResponse

internal interface MediaApi {
    suspend fun download(request: DownloadRequest): Flow<ByteArray>

    suspend fun getGifs(request: GifRequest) : GifResponse
}