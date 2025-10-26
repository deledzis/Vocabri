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
package com.vocabri.notifications.internal

import android.content.Context
import com.vocabri.notifications.PushNotificationTopics
import com.vocabri.notifications.R

internal object NotificationConstants {
    const val DEFAULT_CHANNEL_ID: String = "vocabri_default_channel"
    private val DEFAULT_TOPICS: List<String> = listOf(PushNotificationTopics.GENERAL)
    const val LOG_CHANNEL_INITIALIZER: String = "NotificationChannelInitializer"
    const val LOG_INITIALIZER: String = "PushNotificationInitializer"
    const val LOG_SERVICE: String = "PushNotificationService"
    const val LOG_TOKEN_REPOSITORY: String = "PushNotificationTokenRepository"

    fun defaultTopics(): List<String> = DEFAULT_TOPICS

    fun getDefaultChannelName(context: Context): String = context.getString(
        R.string.notification_channel_general_name,
    )

    fun getDefaultChannelDescription(context: Context): String = context.getString(
        R.string.notification_channel_general_description,
    )
}
