package net.itanchi.addeep.android.ui.screen.home.contacts

import net.itanchi.addeep.core.data.model.User

sealed class ContactsEvent {
    object SyncContacts : ContactsEvent()

    object OpenPhoneSettings : ContactsEvent()

    object SearchFriend : ContactsEvent()

    object InviteFriend : ContactsEvent()

    data class Chat(val contact: User) : ContactsEvent()
}