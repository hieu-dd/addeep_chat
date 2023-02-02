package net.itanchi.addeep.server.rsocket.request

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.repository.message.MessageType

@Serializable
data class MessageRequest(
    val message: String,
    val type: MessageType
)