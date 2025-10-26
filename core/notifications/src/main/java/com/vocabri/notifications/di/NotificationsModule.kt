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
package com.vocabri.notifications.di

import com.vocabri.notifications.channel.AndroidNotificationChannelManager
import com.vocabri.notifications.channel.NotificationChannelManager
import com.vocabri.notifications.deeplink.DeeplinkHandler
import com.vocabri.notifications.deeplink.DeeplinkParser
import com.vocabri.notifications.deeplink.NoOpDeeplinkHandler
import com.vocabri.notifications.image.DefaultNotificationImageLoader
import com.vocabri.notifications.image.NotificationImageLoader
import com.vocabri.notifications.manager.AndroidNotificationManager
import com.vocabri.notifications.manager.NotificationManager
import com.vocabri.notifications.model.NotificationDefaults
import com.vocabri.notifications.permission.NotificationPermissionHandler
import com.vocabri.notifications.service.NotificationMessageParser
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Exposes Koin bindings for the notifications framework.
 */
val notificationsModule: Module = module {
    single { NotificationDefaults() }
    single<NotificationImageLoader> { DefaultNotificationImageLoader(androidContext()) }
    single<NotificationChannelManager> { AndroidNotificationChannelManager(androidContext()) }
    single { DeeplinkParser() }
    single<DeeplinkHandler> { NoOpDeeplinkHandler() }
    single { NotificationPermissionHandler(androidContext()) }
    single { NotificationMessageParser(defaults = get(), deeplinkParser = get()) }
    single<NotificationManager> {
        AndroidNotificationManager(
            context = androidContext(),
            defaults = get(),
            channelManager = get(),
            imageLoader = get(),
            deeplinkHandler = get(),
        )
    }
}
