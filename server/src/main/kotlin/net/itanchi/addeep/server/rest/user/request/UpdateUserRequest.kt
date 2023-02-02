package net.itanchi.addeep.server.rest.user.request

import kotlinx.serialization.Serializable
import net.itanchi.addeep.server.repository.user.Gender
import net.itanchi.addeep.server.utils.serializers.LocalDateSerializer
import net.itanchi.addeep.server.utils.validators.AddeepIdValidator
import net.itanchi.addeep.server.utils.validators.EmailValidator
import net.itanchi.addeep.server.utils.validators.Validator
import net.itanchi.addeep.server.utils.validators.validate
import java.time.LocalDate

@Serializable
data class UpdateUserRequest(
    val addeepId: String? = null,
    val allowToSearchByAddeepId: Boolean? = null,
    val name: String? = null,
    val email: String? = null,
    val gender: Gender? = null,
    @Serializable(with = LocalDateSerializer::class)
    val dob: LocalDate? = null,
    val preferences: Preferences? = null
) {
    init {
        mutableListOf<Validator>().apply {
            email?.let { add(EmailValidator(it)) }
            addeepId?.let { add(AddeepIdValidator(it)) }
        }.validate()
    }
}


@Serializable
data class Preferences(
    var collectAndUsePersonalInfo: Boolean? = null
)