package net.itanchi.addeep.android.ui.screen.home.conversations

sealed class ConversationsEvent {
    data class OpenConversation(
        val conversationId: Long = 0,
        val userId: Long = 0,
        val userName: String = ""
    ) : ConversationsEvent()

    object OpenContactPicker : ConversationsEvent()
}