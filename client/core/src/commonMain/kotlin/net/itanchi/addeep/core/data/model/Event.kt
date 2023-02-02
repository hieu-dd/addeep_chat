package net.itanchi.addeep.core.data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String = "",
    val url: String = "",
    val startedAt: Instant = Clock.System.now(),
    val endedAt: Instant? = null,
    val applyOn: MessageType,
    val status: Status,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
)

enum class Status {
    Active,
    InActive
}