package net.itanchi.addeep.core.data

import co.touchlab.kermit.Logger
import net.itanchi.addeep.core.data.api.MediaApi
import net.itanchi.addeep.core.data.api.request.DownloadRequest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

actual class AppMediaManager : KoinComponent {
    private val mediaApi: MediaApi by inject()

    suspend fun downloadMedia(mediaKey: String, output: File) {
        mediaApi.download(DownloadRequest(mediaKey)).collect {
            Logger.d("Downloading $mediaKey: ${it.size}")
            output.appendBytes(it)
        }
    }
}