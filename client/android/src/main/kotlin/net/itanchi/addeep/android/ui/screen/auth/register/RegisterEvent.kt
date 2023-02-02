package net.itanchi.addeep.android.ui.screen.auth.register

import kotlinx.datetime.LocalDate
import net.itanchi.addeep.core.data.model.Gender


sealed class RegisterEvent {
    data class UploadAvatar(
        val avatar: ByteArray,
    ) : RegisterEvent()

    data class Register(
        val name: String,
        val dob: LocalDate?,
        val gender: Gender?,
    ) : RegisterEvent()

    object DismissError : RegisterEvent()
}