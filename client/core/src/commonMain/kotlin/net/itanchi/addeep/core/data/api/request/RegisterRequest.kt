package net.itanchi.addeep.core.data.api.request

import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterRequest(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
)
