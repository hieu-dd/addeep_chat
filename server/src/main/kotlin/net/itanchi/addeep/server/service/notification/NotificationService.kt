package net.itanchi.addeep.server.service.notification

import net.itanchi.addeep.server.repository.user.DeviceType
import net.itanchi.addeep.server.repository.user.UserDeviceRepo
import net.itanchi.addeep.server.service.conversation.Conversation
import org.springframework.stereotype.Component

@Component
class NotificationService(
    private val userDeviceRepo: UserDeviceRepo,
    private val fcmNotificationService: FcmNotificationService
) {
    suspend fun registerDeviceToken(
        userId: Long,
        deviceToken: String,
        deviceType: DeviceType
    ) {
        userDeviceRepo.updateDeviceToken(
            userId = userId,
            deviceType = deviceType,
            deviceToken = deviceToken
        )
    }


    /**
     * Push notification to device
     * @param userId: userId that want to push. Use null for broadcast message
     * @param data: message data
     */
    suspend fun pushNotification(
        userId: Long? = null,
        data: Conversation
    ) {
        userId?.let {
            userDeviceRepo.findAllByUserId(userId).forEach {
                val message = data.messages.first()
                fcmNotificationService.pushNotification(
                    token = it.deviceToken,
                    data = mapOf(
                        "conversationId" to data.id.toString(),
                        "conversationType" to data.type.toString(),
                        "senderIcon" to "",
                        "senderName" to message.sender.name,
                        "message" to message.message,
                        "messageId" to message.id.toString(),
                        "messageType" to message.type.toString(),
                        "messageTime" to message.createdAt.epochSecond.toString()
                    )
                )
            }
        } ?: let {

            // Send broadcast message
        }
    }
}