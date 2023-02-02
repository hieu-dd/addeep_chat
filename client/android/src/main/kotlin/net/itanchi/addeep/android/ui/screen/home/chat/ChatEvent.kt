package net.itanchi.addeep.android.ui.screen.home.chat

import net.itanchi.addeep.core.data.model.Message
import net.itanchi.addeep.core.data.model.User

sealed class ChatEvent {
    object GoBack : ChatEvent()

    data class NavigateToProfile(
        val user: User,
    ) : ChatEvent()

    data class SendMessage(
        val message: Message,
    ) : ChatEvent()

    data class ClickMessage(
        val message: Message,
    ) : ChatEvent()

    object LoadMoreMessages : ChatEvent()

    data class ReloadConversation(
        val conversationId: ConversationId,
    ) : ChatEvent()

    data class LoadGifs(
        val search: String,
        val isLoadMore: Boolean
    ) : ChatEvent()

    object LoadMedia : ChatEvent()

    object OpenPhoneSettings : ChatEvent()
}