package net.itanchi.addeep.android.ui.screen.home.chat

@JvmInline
value class UserId(
    val value: Long,
)

@JvmInline
value class UserName(
    val value: String,
)

@JvmInline
value class ConversationId(
    val value: Long,
)

fun Long.toUserId() = UserId(this)

fun String.toUserName() = UserName(this)

fun Long.toConversationId() = ConversationId(this)