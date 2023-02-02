package net.itanchi.addeep.server.rest.user.request

import kotlinx.serialization.Serializable

@Serializable
data class AddContactRequest(
    val userId: Long
)