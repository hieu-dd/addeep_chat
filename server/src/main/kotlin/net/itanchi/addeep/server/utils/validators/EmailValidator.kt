package net.itanchi.addeep.server.utils.validators

import net.itanchi.addeep.server.exception.Error

private const val EMAIL_REGEX = (
        "^[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+\$"
        )

class EmailValidator(
    private val email: String?,
    required: Boolean = false,
) : StringValidator(
    obj = email,
    fieldName = "email",
    minLength = STRING_MIN_LENGTH,
    maxLength = STRING_MAX_LENGTH,
    required = required,
) {
    private val matcher = EMAIL_REGEX.toRegex()

    override fun validate(): Error? = super.validate() ?: when {
        email != null && !matcher.matches(email) -> Error.ValidationDataError.InvalidEmail
        else -> null
    }
}