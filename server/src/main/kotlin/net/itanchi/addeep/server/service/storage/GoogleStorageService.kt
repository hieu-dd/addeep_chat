package net.itanchi.addeep.server.service.storage

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import net.itanchi.addeep.server.exception.Error
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.io.FileNotFoundException
import java.net.URLDecoder
import java.nio.ByteBuffer
import java.nio.charset.Charset

@Component
class GoogleStorageService(
    @Qualifier("cloudStorage") private val storage: Storage,
    @Qualifier("customResourceLoader") private val resourceLoader: ResourceLoader
) : StorageService {

    companion object {
        const val DEFAULT_BUCKET_NAME = "addeep-storage"
    }

    override suspend fun uploadFile(
        bucketName: String,
        path: String,
        contentType: String?,
        data: Flow<ByteBuffer>,
        limitSize: Long,
    ): UploadInfo {
        var currentUploadSize = 0L
        val blobId = BlobId.of(bucketName, path)
        val blobInfo = BlobInfo.newBuilder(blobId).apply {
            contentType?.let {
                setContentType(it)
            }
        }.build()
        return try {
            storage.writer(blobInfo)
                .use { writer ->
                    data.collect {
                        currentUploadSize += it.remaining()
                        withContext(Dispatchers.IO) {
                            writer.write(it)
                        }
                        if (currentUploadSize > limitSize)
                            throw Error.UploadFileError.ExceedMaxSize
                    }
                }
            UploadInfo(
                path = blobId.toGsUtilUri(),
                size = currentUploadSize,
                md5 = storage.get(blobId).md5ToHexString
            )
        } catch (ex: Throwable) {
            storage.delete(blobId)
            throw ex
        }
    }

    override suspend fun getResource(path: String): ResourceInfo {
        return try {
            val blobId = BlobId.of(DEFAULT_BUCKET_NAME, path)
            ResourceInfo(
                data = resourceLoader.getResource(
                    URLDecoder.decode(
                        "gs://${DEFAULT_BUCKET_NAME}/$path",
                        Charset.forName("UTF-8")
                    )
                ).inputStream,
                contentType = storage.get(blobId).contentType

            )
        } catch (exception: FileNotFoundException) {
            throw Error.FileNotFound
        }
    }
}