package net.itanchi.addeep.server.rest.user.request

import kotlinx.serialization.Serializable

@Serializable
data class PushTokenRequest(
    val deviceToken: String,
)