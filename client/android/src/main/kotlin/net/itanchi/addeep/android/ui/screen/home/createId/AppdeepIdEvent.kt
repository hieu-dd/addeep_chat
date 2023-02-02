package net.itanchi.addeep.android.ui.screen.home.createId

sealed class AddeepIdEvent {
    object Back : AddeepIdEvent()
    data class RegisterAddeepId(val addeepId: String) : AddeepIdEvent()
    data class ToggleSearchableAddeepId(val enable: Boolean) : AddeepIdEvent()
}