package net.itanchi.addeep.server.rest.event.dto

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.repository.event.Status
import net.itanchi.addeep.server.repository.message.MessageType
import net.itanchi.addeep.server.service.event.Event
import net.itanchi.addeep.server.utils.serializers.InstantSerializer
import java.time.Instant

@Serializable
data class EventDTO(
    val id: Long = 0,
    val name: String,
    val description: String,
    val imageUrl: String,
    val url: String,
    var actions: List<ActionDTO> = listOf(),
    var messageConditions: List<MessageConditionDTO> = listOf(),
    @Serializable(with = InstantSerializer::class)
    val startedAt: Instant = Instant.now(),
    @Serializable(with = InstantSerializer::class)
    val endedAt: Instant? = null,
    val applyOn: MessageType,
    val status: Status,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant = Instant.now(),
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant = Instant.now(),
) {
    companion object {
        fun fromEvent(event: Event): EventDTO = with(event) {
            EventDTO(
                id = id,
                name = name,
                description = description,
                imageUrl = imageUrl,
                url = url,
                actions = actions.map { ActionDTO.fromAction(it) },
                messageConditions = messageConditions.map { MessageConditionDTO.fromMessageCondition(it) },
                startedAt = startedAt,
                endedAt = endedAt,
                applyOn = applyOn,
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }
}