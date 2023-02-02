package net.itanchi.addeep.server.rsocket.request

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.repository.event.ActionType

@Serializable
data class TriggerMessageEventRequest(
    val action: ActionType = ActionType.View
)