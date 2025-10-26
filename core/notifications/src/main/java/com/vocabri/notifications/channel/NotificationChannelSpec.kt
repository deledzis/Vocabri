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

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.vocabri.notifications.model.NotificationSound

/**
 * Declarative configuration used to register an Android notification channel.
 */
data class NotificationChannelSpec(
    val id: String,
    val name: String,
    val description: String,
    val importance: NotificationImportance = NotificationImportance.DEFAULT,
    val showBadge: Boolean = true,
    val enableLights: Boolean = false,
    @ColorInt val lightColor: Int? = null,
    val enableVibration: Boolean = true,
    val vibrationPattern: List<Long> = emptyList(),
    val sound: NotificationSound? = NotificationSound.Default,
    val groupId: String? = null,
    val canBypassDnd: Boolean = false,
    val lockscreenVisibility: Int = NotificationCompat.VISIBILITY_PRIVATE,
) {

    @RequiresApi(Build.VERSION_CODES.O)
    internal fun toNotificationChannel(context: Context): NotificationChannel = NotificationChannel(
        id,
        name,
        importance.channelImportance,
    ).apply {
        description = this@NotificationChannelSpec.description
        setShowBadge(showBadge)
        enableLights(enableLights)
        if (enableLights) {
            lightColor?.let(::setLightColor)
        }
        enableVibration(enableVibration)
        if (enableVibration && vibrationPattern.isNotEmpty()) {
            vibrationPattern = vibrationPattern.toLongArray()
        }
        sound?.resolve(context)?.let { (uri, attributes) ->
            setSound(uri, attributes)
        } ?: setSound(null, null)
        groupId?.let { group = it }
        setBypassDnd(canBypassDnd)
        lockscreenVisibility = this@NotificationChannelSpec.lockscreenVisibility
    }
}
