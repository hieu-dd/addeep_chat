package net.itanchi.addeep.android.ui.screen.permission

sealed class PermissionEvent {
    object NavigateToPhoneSettings : PermissionEvent()
    object NavigateToHome : PermissionEvent()
}