package net.itanchi.addeep.core.data.api.request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import net.itanchi.addeep.core.data.model.Gender
import net.itanchi.addeep.core.data.model.Preferences

@Serializable
internal data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val dob: LocalDate? = null,
    val gender: Gender? = null,
    val preferences: Preferences? = null,
    val addeepId: String? = null,
    val allowToSearchByAddeepId: Boolean? = null,
)