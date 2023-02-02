package net.itanchi.addeep.server.utils.validators

import net.itanchi.addeep.server.exception.Error

private const val ADDEEP_ID_REGEX = (
        "^[a-zA-Z0-9._-]{4,12}$"
        )

class AddeepIdValidator(
    private val addeepId: String?,
    required: Boolean = false,
) : StringValidator(
    obj = addeepId,
    fieldName = "addeepId",
    minLength = ADEEP_ID_MIN_LENGTH,
    maxLength = ADEEP_ID_MAX_LENGTH,
    required = required,
) {

    private val matcher = ADDEEP_ID_REGEX.toRegex()

    override fun validate(): Error? = super.validate() ?: when {
        addeepId != null && !matcher.matches(addeepId) -> Error.ValidationDataError.InvalidAddeepId
        else -> null
    }
}