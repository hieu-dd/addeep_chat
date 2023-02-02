package net.itanchi.addeep.server.utils.validators

import net.itanchi.addeep.server.exception.Error

private const val PHONE_NUMBER_REGEX = ("(\\+[0-9]+[\\- \\.]*)?"  // +<digits><sdd>*
        + "(\\([0-9]+\\)[\\- \\.]*)?"  // (<digits>)<sdd>*
        + "([0-9][0-9\\- \\.]+[0-9])") // <digit><digit|sdd>+<digit>

class PhoneNumberValidator(
    private val phone: String?,
    required: Boolean = false,
) : StringValidator(
    obj = phone,
    fieldName = "telephone",
    minLength = TELEPHONE_MIN_LENGTH,
    maxLength = TELEPHONE_MAX_LENGTH,
    required = required,
) {
    private val matcher = PHONE_NUMBER_REGEX.toRegex()

    override fun validate(): Error? = super.validate() ?: when {
        phone != null && !matcher.matches(phone) -> Error.ValidationDataError.InvalidTelephone
        else -> null
    }
}