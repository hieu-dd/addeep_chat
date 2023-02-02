package net.itanchi.addeep.server.service.conversation

import net.itanchi.addeep.server.repository.conversation.ConversationType
import net.itanchi.addeep.server.service.user.User
import java.time.Instant

class Conversation(
    var id: Long = 0,
    var title: String,
    var type: ConversationType,
    var participants: List<User> = listOf(),
    var messages: List<Message> = listOf(),
    var creator: User,
    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
)