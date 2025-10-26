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
package com.vocabri.notifications.deeplink

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import com.vocabri.notifications.deeplink.DeeplinkData
import com.vocabri.notifications.deeplink.DeeplinkHandler
import com.vocabri.notifications.deeplink.DeeplinkParser

/**
 * Application specific [DeeplinkHandler] that creates task stacks targeting [entryActivity].
 */
class ComposeNotificationDeeplinkHandler(
    private val deeplinkParser: DeeplinkParser,
    private val entryActivity: Class<out Activity>,
) : DeeplinkHandler {

    override fun createContentIntent(context: Context, notificationId: Int, deeplink: DeeplinkData?): PendingIntent? {
        return buildPendingIntent(context, notificationId, deeplink, actionId = null)
    }

    override fun createActionIntent(context: Context, actionId: String, requestCode: Int, deeplink: DeeplinkData?): PendingIntent? {
        return buildPendingIntent(context, requestCode, deeplink, actionId = actionId)
    }

    override fun extract(intent: Intent?): DeeplinkData? {
        if (intent == null) return null
        val rawUri = intent.getStringExtra(EXTRA_DEEPLINK_URI) ?: intent.dataString ?: return null
        intent.removeExtra(EXTRA_DEEPLINK_URI)
        return deeplinkParser.parse(rawUri)
    }

    private fun buildPendingIntent(
        context: Context,
        requestCode: Int,
        deeplink: DeeplinkData?,
        actionId: String?,
    ): PendingIntent {
        val intent = Intent(context, entryActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NOTIFICATION_ID, requestCode)
            actionId?.let { putExtra(EXTRA_ACTION_ID, it) }
            deeplink?.let { putExtra(EXTRA_DEEPLINK_URI, it.rawUri) }
        }
        val stackBuilder = TaskStackBuilder.create(context).apply {
            addNextIntentWithParentStack(intent)
        }
        val finalRequestCode = if (deeplink != null) {
            requestCode
        } else {
            REQUEST_CODE_BASE + requestCode
        }
        return stackBuilder.getPendingIntent(finalRequestCode, PENDING_INTENT_FLAGS)!!
    }

    companion object {
        private const val EXTRA_DEEPLINK_URI = "com.vocabri.notifications.EXTRA_DEEPLINK_URI"
        private const val EXTRA_NOTIFICATION_ID = "com.vocabri.notifications.EXTRA_NOTIFICATION_ID"
        private const val EXTRA_ACTION_ID = "com.vocabri.notifications.EXTRA_ACTION_ID"
        private const val REQUEST_CODE_BASE = 2_000
        private const val PENDING_INTENT_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    }
}
