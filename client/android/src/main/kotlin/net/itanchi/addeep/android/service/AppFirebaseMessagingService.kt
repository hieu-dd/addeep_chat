package net.itanchi.addeep.android.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import co.touchlab.kermit.Logger
import coil.Coil
import coil.request.ImageRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.itanchi.addeep.android.AddeepApp
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.ui.screen.MainActivity
import net.itanchi.addeep.core.data.AppDataManager
import org.koin.android.ext.android.inject

class AppFirebaseMessagingService : FirebaseMessagingService() {
    private val scope = MainScope()
    private val dataManager: AppDataManager by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        Logger.d("onMessageReceived: ${message.data}")

        scope.launch {
            displayNotification(message.data)
        }
    }

    override fun onNewToken(token: String) {
        Logger.d("onNewToken: ${token}")

        scope.launch {
            updatePushToken(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.coroutineContext.cancel()
    }

    private suspend fun updatePushToken(token: String) {
        dataManager.updatePushToken(token)
    }

    private suspend fun displayNotification(data: Map<String, String>) {
        val conversationId = data["conversationId"]?.toInt() ?: 0
        if (conversationId.toLong() == dataManager.latestConversationId && AddeepApp.isAppInForeground) return

        val channelId = getString(R.string.app_channel_id)
        val notificationManager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createMessageNotificationChannel(channelId)
        }
        val notification = createMessageNotification(
            channelId = channelId,
            conversationId = conversationId,
            conversationType = data["conversationType"].orEmpty(),
            senderAvatar = data["senderAvatar"].orEmpty(),
            senderName = data["senderName"].orEmpty(),
            messageType = data["messageType"].orEmpty(),
            message = data["message"].orEmpty(),
            messageTime = data["messageTime"]?.toLong() ?: 0,
        )
        notificationManager.notify(conversationId, notification)
    }

    private suspend fun createMessageNotification(
        channelId: String,
        conversationId: Int,
        conversationType: String,
        senderAvatar: String,
        senderName: String,
        messageType: String,
        message: String,
        messageTime: Long,
    ): Notification {
        val senderIconRequest = ImageRequest.Builder(this)
            .data(senderAvatar)
            .allowHardware(false)
            .build()
        val senderIcon = Coil.imageLoader(this)
            .execute(senderIconRequest)
            .drawable
            ?.let { (it as BitmapDrawable).bitmap }
            ?: AppCompatResources.getDrawable(this, R.drawable.ic_account_circle)!!.toBitmap()

        val sender = Person.Builder()
            .setIcon(IconCompat.createWithBitmap(senderIcon))
            .setName(senderName)
            .build()

        val messageContent = when (messageType) {
            "PlainText" -> message
            "Sticker" -> "Sticker"
            else -> "New message"
        }

        val messageStyle = NotificationCompat.MessagingStyle(sender)
            .addMessage(messageContent, messageTime, sender)
            .setGroupConversation(conversationType == "Group")

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("New message from ${sender.name}")
            .setContentText(messageContent)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setStyle(messageStyle)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(MainActivity.getPendingIntent(this, conversationId.toString()))
            .build()
    }

    private fun NotificationManager.createMessageNotificationChannel(
        channelId: String,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.app_channel_name)
            val channelDescription = getString(R.string.app_channel_description)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = channelDescription
            }

            createNotificationChannel(channel)
        }
    }
}