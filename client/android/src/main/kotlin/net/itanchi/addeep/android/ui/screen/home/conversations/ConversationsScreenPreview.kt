package net.itanchi.addeep.android.ui.screen.home.conversations

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import net.itanchi.addeep.core.data.model.*

@Preview
@Composable
fun ConversationListPreview() {
    ConversationList(
        conversations = listOf()
    ) {}
}

@Preview
@Composable
fun ConversationRowPreview() {
    ConversationRow(
        conversation = Conversation(
            id = 1,
            title = "Chat",
            type = ConversationType.Single,
            creator = User(id = 1),
            participants = listOf(
                User(id = 1, name = "User 1", isMe = false),
                User(id = 2, name = "User 2", isMe = true),
            ),
            messages = listOf(
                Message(
                    id = 1,
                    sender = User(id = 1),
                    type = MessageType.PlainText,
                    message = "Hello",
                    createdAt = Clock.System.now(),
                )
            )
        )
    ) {}
}