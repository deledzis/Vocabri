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
import androidx.annotation.IntRange
import androidx.core.app.NotificationCompat

/**
 * Describes the importance level for notification channels and individual notifications.
 *
 * The enum wraps both the platform channel importance (used when registering [android.app.NotificationChannel])
 * and the compat notification priority used when building [NotificationCompat.Builder].
 */
@Suppress("MagicNumber")
enum class NotificationImportance(
    @IntRange(from = NotificationManager.IMPORTANCE_NONE.toLong(), to = NotificationManager.IMPORTANCE_MAX.toLong())
    val channelImportance: Int,
    val notificationPriority: Int,
) {
    MINIMUM(
        channelImportance = NotificationManager.IMPORTANCE_MIN,
        notificationPriority = NotificationCompat.PRIORITY_MIN,
    ),
    LOW(
        channelImportance = NotificationManager.IMPORTANCE_LOW,
        notificationPriority = NotificationCompat.PRIORITY_LOW,
    ),
    DEFAULT(
        channelImportance = NotificationManager.IMPORTANCE_DEFAULT,
        notificationPriority = NotificationCompat.PRIORITY_DEFAULT,
    ),
    HIGH(
        channelImportance = NotificationManager.IMPORTANCE_HIGH,
        notificationPriority = NotificationCompat.PRIORITY_HIGH,
    ),
    URGENT(
        channelImportance = NotificationManager.IMPORTANCE_MAX,
        notificationPriority = NotificationCompat.PRIORITY_MAX,
    );

    companion object {
        /**
         * Parses an importance string (case-insensitive) into a [NotificationImportance].
         * Defaults to [DEFAULT] when the value is not recognised.
         */
        fun fromString(value: String?): NotificationImportance = when (value?.trim()?.uppercase()) {
            MINIMUM.name -> MINIMUM
            LOW.name -> LOW
            HIGH.name -> HIGH
            URGENT.name -> URGENT
            else -> DEFAULT
        }
    }
}
