package net.itanchi.addeep.android.ui.screen.home.conversations.select_contact

import net.itanchi.addeep.core.data.model.User

sealed class SelectContactEvent {
    object Close : SelectContactEvent()

    data class SelectContact(
        val contact: User,
    ) : SelectContactEvent()
}