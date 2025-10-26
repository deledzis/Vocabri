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
package com.vocabri.notifications

import com.google.firebase.messaging.FirebaseMessaging
import com.vocabri.logger.logger
import com.vocabri.notifications.internal.NotificationConstants
import com.vocabri.notifications.internal.NotificationConstants.LOG_INITIALIZER
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.coroutineContext

/**
 * Firebase-backed implementation that configures notification channels, retrieves the registration token
 * and subscribes to default topics.
 */
class FirebasePushNotificationInitializer(
    private val firebaseMessaging: FirebaseMessaging,
    private val channelInitializer: NotificationChannelInitializer,
    private val permissionManager: PushNotificationPermissionManager,
    private val tokenRepository: PushNotificationTokenRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PushNotificationInitializer {

    private val log = logger(LOG_INITIALIZER)
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val initializationJob = AtomicReference<Job?>()

    override fun initialize() {
        channelInitializer.ensureDefaultChannel()

        if (!permissionManager.hasPermission()) {
            log.w { "Notification permission not granted. Push initialization postponed." }
            return
        }

        firebaseMessaging.isAutoInitEnabled = true

        initializationJob.getAndSet(
            scope.launch {
                val currentJob = coroutineContext[Job]
                try {
                    val token = firebaseMessaging.token.await()
                    log.i { "Firebase Cloud Messaging token obtained" }
                    tokenRepository.update(token)

                    NotificationConstants.defaultTopics().forEach { topic ->
                        runCatching { firebaseMessaging.subscribeToTopic(topic).await() }
                            .onSuccess { log.i { "Subscribed to notifications topic: $topic" } }
                            .onFailure { error ->
                                log.e(error) { "Failed to subscribe to topic: $topic" }
                            }
                    }
                } catch (cancellation: CancellationException) {
                    log.d { "Push notification initialization was cancelled" }
                    throw cancellation
                } catch (error: Exception) {
                    log.e(error) { "Failed to initialize push notifications" }
                } finally {
                    if (currentJob != null) {
                        initializationJob.compareAndSet(currentJob, null)
                    } else {
                        initializationJob.set(null)
                    }
                }
            },
        )?.cancel()
    }
}
