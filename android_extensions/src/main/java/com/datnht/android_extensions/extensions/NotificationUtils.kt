package com.datnht.android_extensions.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * https://codelabs.developers.google.com/codelabs/advanced-android-kotlin-training-notifications/#4
 * Use the NotificationManager class to create, send, update, and cancel a notification using.
 * Use a NotificationChannel object with the createNotificationChannel method to set a channel for the notification.
 * Use addAction() to add quick actions to a notification.
 * Use setShowBadge() to enable or disable badges,.
 * Style your notifications using styles which extends from Notification.Style
 * Set the importance level with NotificationChannel.setImportance()
 */

fun NotificationManager.sendNotification(
    notificationData: NotificationData,
    applicationContext: Context
) {
    // Channel de nhom cac noti. Bang cach nay, user co the control toan bo noti trong cun 1 channel
    // Bat dau tu Api 26, Android 8, bat buoc phai co
    val builder = NotificationCompat.Builder(
        applicationContext,
        notificationData.channelId
    )

    // base info
    builder.setSmallIcon(notificationData.icon)
        .setContentTitle(notificationData.title)
        .setContentText(notificationData.message)
        .setAutoCancel(notificationData.isAutoCancel)

    // main action
    notificationData.contentIntent?.let {
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationData.id,
            it,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(contentPendingIntent)
    }

    // sub action
    notificationData.actions?.run {
        forEach { action ->
            builder.addAction(action.icon, action.title, action.action)
        }
    }

    // noti style
    notificationData.style?.let { builder.setStyle(it) }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && notificationData.priority != null) {
        builder.priority = notificationData.priority
    }
    notify(notificationData.id, builder.build())
}

fun Fragment.getNotificationManager() = ContextCompat.getSystemService(
    this.requireContext(),
    NotificationManager::class.java
) as NotificationManager

fun Fragment.createChannel(channelData: ChannelData) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelData.channelId,
            channelData.channelName,
            channelData.channelImportant
        )

        notificationChannel.enableLights(channelData.isEnableLight)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(channelData.isEnableVibration)
        notificationChannel.description = channelData.channelDes
        notificationChannel.setShowBadge(channelData.isShowBadge)

        val notificationManager = requireActivity().getSystemService(
            NotificationManager::class.java
        )
        notificationManager?.createNotificationChannel(notificationChannel)
    }
}

/**
 * Create init for other style
 */
fun createNotificationStyle(styleData: NotificationStyleData): NotificationCompat.Style? =
    when (styleData) {
        is NotificationStyleData.BigPictureStyle -> {
            NotificationCompat.BigPictureStyle()
                .bigPicture(styleData.pictureImage)
                .bigLargeIcon(styleData.largeIcon)
        }
        else -> null
    }

fun NotificationManager.cancelNotifications() {
    cancelAll()
}

fun convertChannelImportant(channelImportant: Int) = when (channelImportant) {
    NotificationManager.IMPORTANCE_HIGH -> NotificationCompat.PRIORITY_HIGH
    NotificationManager.IMPORTANCE_LOW -> NotificationCompat.PRIORITY_LOW
    NotificationManager.IMPORTANCE_MIN -> NotificationCompat.PRIORITY_MIN
    else -> NotificationCompat.PRIORITY_DEFAULT
}

data class ChannelData(
    val channelId: String,
    val channelName: String,
    val channelDes: String,
    val channelImportant: Int = NotificationManager.IMPORTANCE_DEFAULT,
    val isEnableLight: Boolean = true,
    val isEnableVibration: Boolean = true,
    val isShowBadge: Boolean = true
)

data class NotificationData(
    val id: Int,
    val channelId: String,
    val title: String,
    val message: String,
    val icon: Int,
    val isAutoCancel: Boolean = true,
    val contentIntent: Intent? = null,
    val style: NotificationCompat.Style? = null,
    val actions: List<NotificationActionData>? = null,
    val priority: Int? = null
)

data class NotificationActionData(
    val icon: Int,
    val title: String,
    val action: PendingIntent
)

sealed class NotificationStyleData {
    data class BigPictureStyle(
        val pictureImage: Bitmap,
        val largeIcon: Bitmap?
    ) : NotificationStyleData()
}
