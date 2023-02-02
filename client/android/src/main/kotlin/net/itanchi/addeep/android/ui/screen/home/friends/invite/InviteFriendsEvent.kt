package net.itanchi.addeep.android.ui.screen.home.friends.invite

sealed class InviteFriendsEvent {
    object Back : InviteFriendsEvent()
    data class InviteViaContact(val inviteType: InviteType) : InviteFriendsEvent()
}

enum class InviteType {
    SMS,
    EMAIL
}