package net.itanchi.addeep.server.service.event

import net.itanchi.addeep.server.repository.event.ActionType
import net.itanchi.addeep.server.repository.event.Status
import net.itanchi.addeep.server.repository.message.MessageType
import java.time.Instant

data class Event(
    val id: Long = 0,
    val name: String,
    val description: String,
    val imageUrl: String,
    val url: String,
    val startedAt: Instant = Instant.now(),
    val endedAt: Instant? = null,
    val applyOn: MessageType,
    val status: Status,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) {
    var actions: List<Action> = listOf()
    var messageConditions: List<MessageCondition> = listOf()

    fun isValidForMessageType(
        messageType: MessageType,
        actionType: ActionType,
        stickerId: Long?
    ): Boolean {
        return applyOn == messageType
                && (messageConditions.isEmpty() || messageConditions.any { it.stickerId == stickerId })
                && actions.any { it.type == actionType }
    }
}

data class MessageCondition(
    val stickerId: Long?
)