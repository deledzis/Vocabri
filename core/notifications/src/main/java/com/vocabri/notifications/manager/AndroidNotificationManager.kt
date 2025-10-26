/*
 * MIT License
 *
 * Copyright (c) 2025 Aleksandr Stiagov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.vocabri.notifications.manager

import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vocabri.logger.logger
import com.vocabri.notifications.channel.NotificationChannelManager
import com.vocabri.notifications.channel.NotificationChannelSpec
import com.vocabri.notifications.model.NotificationAction
import com.vocabri.notifications.model.NotificationData
import com.vocabri.notifications.model.NotificationDefaults
import com.vocabri.notifications.model.NotificationImage
import com.vocabri.notifications.model.NotificationSound
import com.vocabri.notifications.model.NotificationStyle
import com.vocabri.notifications.deeplink.DeeplinkHandler
import com.vocabri.notifications.image.NotificationImageLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of [NotificationManager] backed by [NotificationManagerCompat].
 */
class AndroidNotificationManager(
    private val context: Context,
    private val defaults: NotificationDefaults,
    private val channelManager: NotificationChannelManager,
    private val imageLoader: NotificationImageLoader,
    private val deeplinkHandler: DeeplinkHandler,
    private val notificationManagerCompat: NotificationManagerCompat = NotificationManagerCompat.from(context),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : NotificationManager {

    private val log = logger()

    init {
        channelManager.ensureChannel(defaults.defaultChannel)
    }

    override suspend fun show(notification: NotificationData) {
        show(listOf(notification))
    }

    override suspend fun show(notifications: Collection<NotificationData>) {
        notifications.forEach { notification ->
            ensureDefaultChannel(notification)
            val builder = NotificationCompat.Builder(context, notification.channelId)
                .setSmallIcon(notification.smallIconResId)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setPriority(notification.importance.notificationPriority)
                .setAutoCancel(notification.autoCancel)
                .setOngoing(notification.ongoing)
                .setOnlyAlertOnce(true)

            notification.category?.let(builder::setCategory)
            notification.subText?.let(builder::setSubText)
            notification.sortKey?.let(builder::setSortKey)
            notification.badgeCount?.let(builder::setNumber)
            notification.timeoutAfterMillis?.let(builder::setTimeoutAfter)
            notification.timestamp?.let(builder::setWhen)
            builder.setShowWhen(notification.showTimestamp)

            if (notification.showBadge) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            }

            notification.groupKey?.let { groupKey ->
                builder.setGroup(groupKey)
                builder.setGroupSummary(notification.isGroupSummary)
            }

            applySound(notification, builder)
            notification.vibrationPattern?.takeIf { it.isNotEmpty() }?.let(builder::setVibrate)

            notification.deeplink?.let {
                deeplinkHandler.createContentIntent(context, notification.id, it)
            }?.let(builder::setContentIntent)

            notification.actions.forEach { action ->
                addAction(builder, notification, action)
            }

            withContext(ioDispatcher) {
                builder.setLargeIcon(loadBitmap(notification.largeIcon))
                applyStyle(notification, builder)
            }

            log.i { "Showing notification ${notification.id} on channel ${notification.channelId}" }
            notificationManagerCompat.notify(notification.tag, notification.id, builder.build())
        }
    }

    override fun cancel(notificationId: Int, tag: String?) {
        log.i { "Cancelling notification $notificationId" }
        notificationManagerCompat.cancel(tag, notificationId)
    }

    override fun cancelAll() {
        log.i { "Cancelling all notifications" }
        notificationManagerCompat.cancelAll()
    }

    override fun ensureChannel(channel: NotificationChannelSpec) {
        channelManager.ensureChannel(channel)
    }

    override fun ensureChannels(channels: Collection<NotificationChannelSpec>) {
        channelManager.ensureChannels(channels)
    }

    override fun areNotificationsEnabled(): Boolean = notificationManagerCompat.areNotificationsEnabled()

    private suspend fun loadBitmap(image: NotificationImage?): android.graphics.Bitmap? = imageLoader.load(image)

    private suspend fun applyStyle(notification: NotificationData, builder: NotificationCompat.Builder) {
        when (val style = notification.style) {
            is NotificationStyle.BigText -> builder.setStyle(
                NotificationCompat.BigTextStyle().bigText(style.text).apply {
                    style.summaryText?.let(::setSummaryText)
                },
            )
            is NotificationStyle.BigPicture -> {
                val bigPictureBitmap = loadBitmap(notification.bigPicture)
                if (bigPictureBitmap != null) {
                    builder.setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(bigPictureBitmap)
                            .bigLargeIcon(null)
                            .apply {
                                style.summaryText?.let(::setSummaryText)
                            },
                    )
                }
            }
            is NotificationStyle.Inbox -> builder.setStyle(
                NotificationCompat.InboxStyle().apply {
                    style.lines.forEach(::addLine)
                    style.summaryText?.let(::setSummaryText)
                },
            )
            null -> {
                // no-op
            }
        }
    }

    private fun addAction(
        builder: NotificationCompat.Builder,
        notification: NotificationData,
        action: NotificationAction,
    ) {
        val pendingIntent = action.deeplink?.let {
            deeplinkHandler.createActionIntent(context, action.id, action.requestCode, it)
        }
        if (pendingIntent == null) {
            log.w { "Skipping action ${action.id} due to missing pending intent" }
            return
        }
        builder.addAction(action.iconResId ?: 0, action.title, pendingIntent)
    }

    private fun ensureDefaultChannel(notification: NotificationData) {
        if (notification.channelId == defaults.defaultChannel.id) {
            channelManager.ensureChannel(defaults.defaultChannel)
        }
    }

    private fun applySound(notification: NotificationData, builder: NotificationCompat.Builder) {
        when (val sound = notification.sound) {
            null -> {
                // do nothing, rely on channel sound
            }
            is NotificationSound.Silent -> builder.setSound(null)
            else -> {
                val (uri, attributes) = sound.resolve(context)
                builder.setSound(uri, attributes)
            }
        }
    }
}
