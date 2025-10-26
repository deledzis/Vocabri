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
import com.vocabri.notifications.channel.NotificationImportance
import com.vocabri.notifications.deeplink.DeeplinkData

/**
 * Immutable notification descriptor consumed by [com.vocabri.notifications.manager.NotificationManager].
 */
class NotificationData private constructor(
    val id: Int,
    val tag: String?,
    val channelId: String,
    val title: CharSequence?,
    val body: CharSequence?,
    val importance: NotificationImportance,
    @DrawableRes val smallIconResId: Int,
    val largeIcon: NotificationImage?,
    val bigPicture: NotificationImage?,
    val style: NotificationStyle?,
    val deeplink: DeeplinkData?,
    val actions: List<NotificationAction>,
    val sound: NotificationSound?,
    val vibrationPattern: LongArray?,
    val groupKey: String?,
    val isGroupSummary: Boolean,
    val showBadge: Boolean,
    val badgeCount: Int?,
    val autoCancel: Boolean,
    val ongoing: Boolean,
    val category: String?,
    val subText: CharSequence?,
    val sortKey: String?,
    val timeoutAfterMillis: Long?,
    val timestamp: Long?,
    val showTimestamp: Boolean,
) {
    companion object {
        fun builder(
            defaults: NotificationDefaults,
            notificationId: Int = defaults.nextNotificationId(),
            channelId: String = defaults.defaultChannel.id,
        ): Builder = Builder(
            defaults = defaults,
            notificationId = notificationId,
            channelId = channelId,
        )
    }

    class Builder internal constructor(
        private val defaults: NotificationDefaults,
        private val notificationId: Int,
        private var channelId: String,
    ) {
        private var tag: String? = null
        private var title: CharSequence? = null
        private var body: CharSequence? = null
        private var importance: NotificationImportance = defaults.defaultChannel.importance
        private var smallIconResId: Int = defaults.defaultSmallIconResId
        private var largeIcon: NotificationImage? = null
        private var bigPicture: NotificationImage? = null
        private var style: NotificationStyle? = null
        private var deeplink: DeeplinkData? = null
        private val actions: MutableList<NotificationAction> = mutableListOf()
        private var sound: NotificationSound? = NotificationSound.Default
        private var vibrationPattern: LongArray? = null
        private var groupKey: String? = defaults.defaultGroupKey
        private var isGroupSummary: Boolean = false
        private var showBadge: Boolean = true
        private var badgeCount: Int? = null
        private var autoCancel: Boolean = true
        private var ongoing: Boolean = false
        private var category: String? = null
        private var subText: CharSequence? = null
        private var sortKey: String? = null
        private var timeoutAfterMillis: Long? = null
        private var timestamp: Long? = System.currentTimeMillis()
        private var showTimestamp: Boolean = true

        fun tag(tag: String?) = apply { this.tag = tag }

        fun title(title: CharSequence?) = apply { this.title = title }

        fun body(body: CharSequence?) = apply { this.body = body }

        fun importance(importance: NotificationImportance) = apply { this.importance = importance }

        fun channelId(channelId: String) = apply { this.channelId = channelId }

        fun smallIcon(@DrawableRes iconResId: Int) = apply { this.smallIconResId = iconResId }

        fun largeIcon(image: NotificationImage?) = apply { this.largeIcon = image }

        fun bigPicture(image: NotificationImage?) = apply { this.bigPicture = image }

        fun style(style: NotificationStyle?) = apply { this.style = style }

        fun deeplink(deeplink: DeeplinkData?) = apply { this.deeplink = deeplink }

        fun addAction(action: NotificationAction) = apply { actions += action }

        fun actions(actions: List<NotificationAction>) = apply {
            this.actions.clear()
            this.actions += actions
        }

        fun sound(sound: NotificationSound?) = apply { this.sound = sound }

        fun vibrationPattern(pattern: LongArray?) = apply { this.vibrationPattern = pattern }

        fun group(groupKey: String?, isGroupSummary: Boolean = false) = apply {
            this.groupKey = groupKey
            this.isGroupSummary = isGroupSummary
        }

        fun showBadge(show: Boolean) = apply { this.showBadge = show }

        fun badgeCount(count: Int?) = apply { this.badgeCount = count }

        fun autoCancel(autoCancel: Boolean) = apply { this.autoCancel = autoCancel }

        fun ongoing(ongoing: Boolean) = apply { this.ongoing = ongoing }

        fun category(category: String?) = apply { this.category = category }

        fun subText(subText: CharSequence?) = apply { this.subText = subText }

        fun sortKey(sortKey: String?) = apply { this.sortKey = sortKey }

        fun timeoutAfter(timeoutMillis: Long?) = apply { this.timeoutAfterMillis = timeoutMillis }

        fun timestamp(timestamp: Long?, showTimestamp: Boolean = true) = apply {
            this.timestamp = timestamp
            this.showTimestamp = showTimestamp
        }

        fun build(): NotificationData {
            require(channelId.isNotBlank()) { "Channel id must not be blank" }
            return NotificationData(
                id = notificationId,
                tag = tag,
                channelId = channelId,
                title = title,
                body = body,
                importance = importance,
                smallIconResId = smallIconResId,
                largeIcon = largeIcon,
                bigPicture = bigPicture,
                style = style,
                deeplink = deeplink,
                actions = actions.toList(),
                sound = sound,
                vibrationPattern = vibrationPattern,
                groupKey = groupKey,
                isGroupSummary = isGroupSummary,
                showBadge = showBadge,
                badgeCount = badgeCount,
                autoCancel = autoCancel,
                ongoing = ongoing,
                category = category,
                subText = subText,
                sortKey = sortKey,
                timeoutAfterMillis = timeoutAfterMillis,
                timestamp = timestamp,
                showTimestamp = showTimestamp,
            )
        }
    }
}
