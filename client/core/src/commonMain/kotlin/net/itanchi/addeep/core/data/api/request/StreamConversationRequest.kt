package net.itanchi.addeep.core.data.api.request

import kotlinx.serialization.Serializable

@Serializable
internal data class StreamConversationRequest(
    val channelId: String,
)
