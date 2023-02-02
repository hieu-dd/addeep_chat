package net.itanchi.addeep.server.utils.validators
import net.itanchi.addeep.server.exception.Error
import net.itanchi.addeep.server.exception.Error.ValidationDataError

open class StringValidator(
    private val obj: String?,
    private val fieldName: String,
    private val minLength: Int? = 0,
    private val maxLength: Int? = Int.MAX_VALUE,
    private val required: Boolean = true,
) : Validator() {
    override fun validate(): Error? = super.validate() ?: when {
        obj.isNullOrBlank() && required ->
            ValidationDataError.FieldIsInvalid(fieldName)
        obj == "null" ->
            ValidationDataError.FieldIsInvalid(fieldName)
        obj != null && minLength != null && obj.length < minLength ->
            ValidationDataError.FieldLengthOutOfRange(fieldName, minLength.toString(), maxLength.toString())
        obj != null && maxLength != null && obj.length > maxLength ->
            ValidationDataError.FieldLengthOutOfRange(fieldName, minLength.toString(), maxLength.toString())
        else -> null
    }
}