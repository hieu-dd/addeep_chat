package net.itanchi.addeep.android.ui.screen.auth.login

sealed class LoginEvent {
    object OpenCountryList : LoginEvent()
    data class SendOTP(
        val phone: String,
    ) : LoginEvent()
}