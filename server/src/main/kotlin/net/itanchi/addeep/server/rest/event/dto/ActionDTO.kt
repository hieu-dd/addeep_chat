package net.itanchi.addeep.server.rest.event.dto

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.repository.event.ActionType
import net.itanchi.addeep.server.service.event.Action

@Serializable
data class ActionDTO(
    val type: ActionType,
    val points: Long
) {
    companion object {
        fun fromAction(action: Action): ActionDTO = with(action) {
            ActionDTO(
                type = type,
                points = points,
            )
        }
    }
}