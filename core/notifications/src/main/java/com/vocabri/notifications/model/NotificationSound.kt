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

import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import androidx.annotation.RawRes
import androidx.core.net.toUri

/**
 * Represents a sound configuration used for notification channels and individual notifications.
 */
sealed class NotificationSound {
    /** Uses system default notification sound. */
    data object Default : NotificationSound()

    /** Disables sound for the notification. */
    data object Silent : NotificationSound()

    /** Plays a specific [Uri] provided by the caller. */
    data class Custom(val uri: Uri, val audioAttributes: AudioAttributes? = defaultAudioAttributes()) :
        NotificationSound()

    /** Plays a raw resource associated with the client application. */
    data class Resource(
        @field:RawRes val resId: Int,
        val audioAttributes: AudioAttributes? = defaultAudioAttributes(),
    ) : NotificationSound()

    internal fun resolve(context: Context): Pair<Uri?, AudioAttributes?> = when (this) {
        Default -> DEFAULT_SOUND
        Silent -> SILENT_SOUND
        is Custom -> uri to audioAttributes
        is Resource -> context.resourceUri(resId) to audioAttributes
    }

    companion object {
        private val DEFAULT_SOUND = Pair(
            android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
            defaultAudioAttributes(),
        )
        private val SILENT_SOUND = Pair<Uri?, AudioAttributes?>(null, null)

        /**
         * Creates the default [AudioAttributes] used when the caller does not provide one explicitly.
         */
        fun defaultAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
    }
}

private fun Context.resourceUri(@RawRes resId: Int): Uri =
    "${android.content.ContentResolver.SCHEME_ANDROID_RESOURCE}://$packageName/$resId".toUri()
