package net.itanchi.addeep.server.utils.validators

import net.itanchi.addeep.server.exception.Error

abstract class Validator {
    companion object{
        const val STRING_MIN_LENGTH = 0
        const val STRING_MAX_LENGTH = 255
        const val TELEPHONE_MIN_LENGTH = 10
        const val TELEPHONE_MAX_LENGTH = 19
        const val ADEEP_ID_MIN_LENGTH  = 4
        const val ADEEP_ID_MAX_LENGTH  = 20
    }

    open fun validate(): Error? {
        return null
    }
}

fun List<Validator>.validate() {
    forEach { validator ->
        val error = validator.validate()
        if (error != null) throw error
    }
}