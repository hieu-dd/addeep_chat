package net.itanchi.addeep.server.service.event

import net.itanchi.addeep.server.repository.event.ActionType

data class Action(
    val type: ActionType,
    val points: Long
)