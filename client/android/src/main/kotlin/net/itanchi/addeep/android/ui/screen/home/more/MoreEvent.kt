package net.itanchi.addeep.android.ui.screen.home.more

sealed class MoreEvent {
    object NavigateToSettings : MoreEvent()
    object NavigateToProfile : MoreEvent()
    object NavigateToPointsHistory : MoreEvent()
}