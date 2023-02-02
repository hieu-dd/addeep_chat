package net.itanchi.addeep.server.service.conversation

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer

data class Attachments(
    val files: List<FileAttachment> = listOf()
)

data class FileAttachment(
    val filename: String,
    val contentType: String?,
    val contentLength: Long,
    val data: Flow<DataBuffer>,
)