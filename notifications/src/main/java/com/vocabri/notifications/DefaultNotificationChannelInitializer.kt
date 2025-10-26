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
package com.vocabri.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.vocabri.logger.logger
import com.vocabri.notifications.internal.NotificationConstants.DEFAULT_CHANNEL_ID
import com.vocabri.notifications.internal.NotificationConstants.LOG_CHANNEL_INITIALIZER
import com.vocabri.notifications.internal.NotificationConstants.getDefaultChannelDescription
import com.vocabri.notifications.internal.NotificationConstants.getDefaultChannelName

/**
 * Creates the notification channel used for general application updates.
 */
class DefaultNotificationChannelInitializer(private val context: Context) : NotificationChannelInitializer {

    private val log = logger(LOG_CHANNEL_INITIALIZER)

    override fun ensureDefaultChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            log.d { "Notification channels are not supported on this API level" }
            return
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        if (notificationManager == null) {
            log.w { "NotificationManager service is unavailable, channel cannot be created" }
            return
        }

        val channel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            getDefaultChannelName(context),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getDefaultChannelDescription(context)
        }

        notificationManager.createNotificationChannel(channel)
        log.i { "Default notification channel ensured" }
    }
}
