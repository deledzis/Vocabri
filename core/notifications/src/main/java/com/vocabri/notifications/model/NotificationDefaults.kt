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
package com.vocabri.notifications.model

import androidx.annotation.DrawableRes
import com.vocabri.notifications.channel.NotificationChannelSpec
import com.vocabri.notifications.channel.NotificationImportance
import java.util.concurrent.atomic.AtomicInteger

/**
 * Holds global defaults for the notifications framework.
 */
data class NotificationDefaults(
    val defaultChannel: NotificationChannelSpec = NotificationChannelSpec(
        id = DEFAULT_CHANNEL_ID,
        name = "General updates",
        description = "All Vocabri notifications",
        importance = NotificationImportance.DEFAULT,
    ),
    @DrawableRes val defaultSmallIconResId: Int = android.R.drawable.ic_dialog_info,
    val defaultGroupKey: String? = null,
    private val initialNotificationId: Int = 1_000,
) {
    private val notificationIdGenerator = AtomicInteger(initialNotificationId)

    fun nextNotificationId(): Int = notificationIdGenerator.incrementAndGet()

    companion object {
        const val DEFAULT_CHANNEL_ID = "vocabri.core.notifications.default"
    }
}
