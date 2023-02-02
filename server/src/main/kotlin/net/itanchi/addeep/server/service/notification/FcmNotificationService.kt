package net.itanchi.addeep.server.service.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import net.itanchi.addeep.server.repository.conversation.ConversationType
import net.itanchi.addeep.server.repository.message.MessageType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class FcmNotificationService {
    private val logger = LoggerFactory.getLogger(FcmNotificationService::class.java)

    fun pushNotification(
        token: String,
        data: Map<String, String>,
    ): String? {
        val fcmMessage: Message = Message.builder()
            .putAllData(data)
            .setToken(token).build()
        var fcmMessageId: String? = null
        try {
            fcmMessageId = FirebaseMessaging.getInstance().send(fcmMessage)
        } catch (exception: FirebaseMessagingException) {
            logger.debug("Push notification to $token fail with exception $exception")
        }
        return fcmMessageId
    }
}