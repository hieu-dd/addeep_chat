package net.itanchi.addeep.server.service.storage

import java.io.InputStream

data class ResourceInfo(
    val data : InputStream,
    val contentType: String
)