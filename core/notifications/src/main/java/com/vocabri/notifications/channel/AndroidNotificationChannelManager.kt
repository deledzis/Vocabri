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
package com.vocabri.notifications.channel

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.VisibleForTesting
import com.vocabri.logger.logger

/**
 * Android specific implementation of [NotificationChannelManager].
 */
class AndroidNotificationChannelManager internal constructor(
    private val context: Context,
    private val notificationManager: NotificationManager,
) : NotificationChannelManager {

    private val log = logger()

    constructor(context: Context) : this(
        context = context,
        notificationManager = context.getSystemService(NotificationManager::class.java),
    )

    override fun ensureChannel(channel: NotificationChannelSpec) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            log.v { "Skipping channel ${channel.id} creation on API < 26" }
            return
        }
        val notificationChannel = channel.toNotificationChannel(context)
        log.d { "Creating notification channel ${channel.id}" }
        notificationManager.createNotificationChannel(notificationChannel)
    }

    override fun ensureChannels(channels: Collection<NotificationChannelSpec>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            log.v { "Skipping channel batch creation on API < 26" }
            return
        }
        val androidChannels = channels.map { it.toNotificationChannel(context) }
        log.d { "Creating ${androidChannels.size} notification channels" }
        notificationManager.createNotificationChannels(androidChannels)
    }

    override fun deleteChannel(channelId: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            log.v { "Skipping channel deletion on API < 26" }
            return
        }
        log.d { "Deleting notification channel $channelId" }
        notificationManager.deleteNotificationChannel(channelId)
    }

    override fun listChannelIds(): List<String> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return emptyList()
        }
        return notificationManager.notificationChannels.map { it.id }
    }

    @VisibleForTesting
    internal fun getNotificationManager(): NotificationManager = notificationManager
}
