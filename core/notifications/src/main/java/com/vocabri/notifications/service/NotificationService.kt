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

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vocabri.logger.logger
import com.vocabri.notifications.manager.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Entry point for Firebase Cloud Messaging push notifications.
 */
class NotificationService : FirebaseMessagingService() {
    private val log = logger()

    private val notificationManager: NotificationManager by inject()
    private val messageParser: NotificationMessageParser by inject()

    private val supervisorJob = SupervisorJob()
    private val serviceScope: CoroutineScope = CoroutineScope(supervisorJob + Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        log.i { "Received remote message: ${message.messageId}" }
        serviceScope.launch {
            val notification = messageParser.parse(message)
            if (notification != null) {
                notificationManager.show(notification)
            } else {
                log.w { "Skipping message because parsing returned null" }
            }
        }
    }

    override fun onNewToken(token: String) {
        log.i { "FCM token refreshed" }
        super.onNewToken(token)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        supervisorJob.cancel()
    }
}
