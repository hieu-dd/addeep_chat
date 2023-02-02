package net.itanchi.addeep.core.data.api.request

import kotlinx.serialization.Serializable

@Serializable
internal data class GetUserRequest(val addeepId: String?, val phone: String?)