package net.itanchi.addeep.android.ui.screen.home.settings

sealed class SettingsEvent {
    object GoBack : SettingsEvent()
    object Logout : SettingsEvent()
    object DismissError: SettingsEvent()
}