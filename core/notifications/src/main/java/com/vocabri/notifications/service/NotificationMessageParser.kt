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
package com.vocabri.notifications.service

import androidx.core.net.toUri
import com.google.firebase.messaging.RemoteMessage
import com.vocabri.logger.logger
import com.vocabri.notifications.channel.NotificationImportance
import com.vocabri.notifications.deeplink.DeeplinkParser
import com.vocabri.notifications.model.NotificationAction
import com.vocabri.notifications.model.NotificationData
import com.vocabri.notifications.model.NotificationDefaults
import com.vocabri.notifications.model.NotificationImage
import com.vocabri.notifications.model.NotificationSound
import com.vocabri.notifications.model.NotificationStyle

/**
 * Converts FCM [RemoteMessage] payloads into [NotificationData].
 */
class NotificationMessageParser(
    private val defaults: NotificationDefaults,
    private val deeplinkParser: DeeplinkParser,
) {
    private val log = logger()

    fun parse(message: RemoteMessage): NotificationData? {
        val payload = message.data
        val notification = message.notification
        if (payload.isEmpty() && notification == null) {
            log.w { "Skipping remote message without notification payload" }
            return null
        }
        val channelId = payload[KEY_CHANNEL_ID].orEmpty().ifBlank { defaults.defaultChannel.id }
        val resolvedImportance = NotificationImportance.fromString(payload[KEY_IMPORTANCE])
        val resolvedId = payload[KEY_NOTIFICATION_ID]?.toIntOrNull()
            ?: message.messageId?.hashCode()
            ?: defaults.nextNotificationId()

        val builder = NotificationData.builder(
            defaults = defaults,
            notificationId = resolvedId,
            channelId = channelId,
        ).importance(resolvedImportance)

        val title = payload[KEY_TITLE] ?: notification?.title
        val body = payload[KEY_BODY] ?: notification?.body
        builder.title(title).body(body)

        builder.deeplink(deeplinkParser.parse(payload[KEY_DEEPLINK] ?: notification?.link?.toString()))

        parseImage(payload[KEY_LARGE_ICON])?.let(builder::largeIcon)
        parseImage(payload[KEY_BIG_PICTURE])?.let(builder::bigPicture)

        when (payload[KEY_STYLE]?.lowercase()) {
            STYLE_BIG_TEXT -> builder.style(NotificationStyle.BigText(payload[KEY_BIG_TEXT] ?: body.orEmpty()))
            STYLE_BIG_PICTURE -> builder.style(
                NotificationStyle.BigPicture(
                    summaryText = payload[KEY_SUMMARY_TEXT],
                ),
            )

            STYLE_INBOX -> builder.style(NotificationStyle.Inbox(parseInboxLines(payload)))
        }

        payload[KEY_SOUND]?.let { builder.sound(parseSound(it)) }
        payload[KEY_VIBRATION]?.let { builder.vibrationPattern(parseVibrationPattern(it)) }
        payload[KEY_GROUP_KEY]?.let { groupKey ->
            val isSummary = payload[KEY_GROUP_SUMMARY]?.toBooleanStrictOrNull() ?: false
            builder.group(groupKey, isSummary)
        }
        payload[KEY_BADGE_COUNT]?.toIntOrNull()?.let(builder::badgeCount)
        payload[KEY_AUTO_CANCEL]?.toBooleanStrictOrNull()?.let(builder::autoCancel)
        payload[KEY_ONGOING]?.toBooleanStrictOrNull()?.let(builder::ongoing)
        payload[KEY_CATEGORY]?.let(builder::category)
        payload[KEY_SUBTEXT]?.let(builder::subText)
        payload[KEY_SORT_KEY]?.let(builder::sortKey)
        payload[KEY_TIMEOUT]?.toLongOrNull()?.let { timeout -> builder.timeoutAfter(timeout) }

        parseActions(payload).forEach(builder::addAction)

        return builder.build()
    }

    private fun parseImage(value: String?): NotificationImage? {
        if (value.isNullOrBlank()) return null
        val trimmed = value.trim()
        return when {
            trimmed.startsWith("http", ignoreCase = true) -> NotificationImage.UrlImage(trimmed)
            trimmed.startsWith("content://") || trimmed.startsWith("file://") ||
                trimmed.startsWith("android.resource://") ->
                NotificationImage.UriImage(trimmed.toUri())

            trimmed.all { it.isDigit() } -> NotificationImage.Resource(trimmed.toInt())
            else -> null
        }
    }

    private fun parseSound(value: String): NotificationSound? = when (value.lowercase()) {
        SOUND_DEFAULT -> NotificationSound.Default
        SOUND_SILENT -> NotificationSound.Silent
        else -> NotificationSound.Custom(value.toUri())
    }

    private fun parseVibrationPattern(value: String): LongArray? = runCatching {
        value.split(',')
            .mapNotNull { it.trim().toLongOrNull() }
            .takeIf { it.isNotEmpty() }
            ?.toLongArray()
    }.getOrNull()

    private fun parseInboxLines(payload: Map<String, String>): List<CharSequence> = buildList {
        var index = 1
        while (true) {
            val value = payload["$KEY_INBOX_LINE_PREFIX$index"] ?: break
            add(value)
            index++
        }
    }

    private fun parseActions(payload: Map<String, String>): List<NotificationAction> = buildList {
        for (index in 1..MAX_ACTIONS) {
            val prefix = "$KEY_ACTION_PREFIX$index"
            val title = payload["${prefix}_title"].orEmpty()
            if (title.isBlank()) continue
            val id = payload["${prefix}_id"] ?: "action_$index"
            val deeplink = deeplinkParser.parse(payload["${prefix}_deeplink"])
            val icon = payload["${prefix}_icon"]?.toIntOrNull()
            add(
                NotificationAction(
                    id = id,
                    title = title,
                    deeplink = deeplink,
                    iconResId = icon,
                    requestCode = payload["${prefix}_requestCode"]?.toIntOrNull() ?: id.hashCode(),
                    requiresAuthentication = payload["${prefix}_requiresAuth"]?.toBooleanStrictOrNull() ?: false,
                    isDestructive = payload["${prefix}_destructive"]?.toBooleanStrictOrNull() ?: false,
                ),
            )
        }
    }

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_BODY = "body"
        private const val KEY_CHANNEL_ID = "channel_id"
        private const val KEY_IMPORTANCE = "importance"
        private const val KEY_NOTIFICATION_ID = "notification_id"
        private const val KEY_DEEPLINK = "deeplink"
        private const val KEY_LARGE_ICON = "large_icon"
        private const val KEY_BIG_PICTURE = "big_picture"
        private const val KEY_STYLE = "style"
        private const val KEY_BIG_TEXT = "big_text"
        private const val KEY_SUMMARY_TEXT = "summary_text"
        private const val KEY_VIBRATION = "vibration"
        private const val KEY_GROUP_KEY = "group"
        private const val KEY_GROUP_SUMMARY = "group_summary"
        private const val KEY_BADGE_COUNT = "badge_count"
        private const val KEY_AUTO_CANCEL = "auto_cancel"
        private const val KEY_ONGOING = "ongoing"
        private const val KEY_CATEGORY = "category"
        private const val KEY_SUBTEXT = "sub_text"
        private const val KEY_SORT_KEY = "sort_key"
        private const val KEY_TIMEOUT = "timeout"
        private const val KEY_SOUND = "sound"
        private const val KEY_INBOX_LINE_PREFIX = "inbox_line_"
        private const val KEY_ACTION_PREFIX = "action"

        private const val STYLE_BIG_TEXT = "big_text"
        private const val STYLE_BIG_PICTURE = "big_picture"
        private const val STYLE_INBOX = "inbox"

        private const val SOUND_DEFAULT = "default"
        private const val SOUND_SILENT = "silent"

        private const val MAX_ACTIONS = 3
    }
}
