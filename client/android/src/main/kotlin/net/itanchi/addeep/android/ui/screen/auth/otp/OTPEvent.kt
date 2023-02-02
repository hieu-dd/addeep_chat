package net.itanchi.addeep.android.ui.screen.auth.otp

sealed class OTPEvent {
    data class Login(val token: String) : OTPEvent()
    object DismissError: OTPEvent()
}