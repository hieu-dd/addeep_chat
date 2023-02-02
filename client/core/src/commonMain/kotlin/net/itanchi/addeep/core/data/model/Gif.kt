package net.itanchi.addeep.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Gif (
    val dataSource: String,
    val url: String,
)