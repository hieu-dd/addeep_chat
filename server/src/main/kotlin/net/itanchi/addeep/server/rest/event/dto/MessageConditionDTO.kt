package net.itanchi.addeep.server.rest.event.dto

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.service.event.MessageCondition

@Serializable
data class MessageConditionDTO(
    val stickerId: Long?
) {
    companion object {
        fun fromMessageCondition(messageCondition: MessageCondition): MessageConditionDTO = with(messageCondition) {
            MessageConditionDTO(
                stickerId = stickerId,
            )
        }
    }
}