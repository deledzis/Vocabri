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
import com.vocabri.notifications.PushNotificationServiceLocator
import com.vocabri.notifications.internal.NotificationConstants

/**
 * Firebase messaging service that propagates token updates into the application layer.
 */
class VocabriFirebaseMessagingService : FirebaseMessagingService() {

    private val log = logger(NotificationConstants.LOG_SERVICE)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        log.i { "Received refreshed Firebase token" }
        val repository = PushNotificationServiceLocator.tokenRepository()
        if (repository == null) {
            log.w { "PushNotificationTokenRepository is not yet available" }
            return
        }
        repository.update(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        log.i { "Push notification received from ${message.from}" }
    }
}
