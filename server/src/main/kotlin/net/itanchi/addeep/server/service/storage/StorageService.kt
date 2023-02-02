package net.itanchi.addeep.server.service.storage

import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.nio.ByteBuffer

interface StorageService {
    suspend fun uploadFile(
        bucketName: String,
        path: String,
        contentType: String?,
        data: Flow<ByteBuffer>,
        limitSize: Long,
    ): UploadInfo

    suspend fun getResource(
        path: String
    ): ResourceInfo
}

data class UploadInfo(
    val path: String,
    val size: Long,
    val md5: String
)