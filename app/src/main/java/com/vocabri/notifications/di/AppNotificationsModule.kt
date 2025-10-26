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

import com.vocabri.notifications.deeplink.ComposeNotificationDeeplinkHandler
import com.vocabri.notifications.deeplink.DeeplinkHandler
import com.vocabri.notifications.model.NotificationDefaults
import com.vocabri.notifications.channel.NotificationChannelSpec
import com.vocabri.notifications.channel.NotificationImportance
import com.vocabri.notifications.navigation.NotificationDeeplinkNavigator
import com.vocabri.notifications.navigation.NotificationNavigationCoordinator
import com.vocabri.ui.main.MainActivity
import com.vocabri.R
import org.koin.dsl.module

val appNotificationsModule = module {
    single<NotificationDefaults>(override = true) {
        NotificationDefaults(
            defaultChannel = NotificationChannelSpec(
                id = "vocabri.general",
                name = "General notifications",
                description = "Updates from your Vocabri vocabulary journey",
                importance = NotificationImportance.DEFAULT,
            ),
            defaultSmallIconResId = R.drawable.ic_notification_default,
            defaultGroupKey = "vocabri.general.group",
        )
    }

    single<DeeplinkHandler>(override = true) {
        ComposeNotificationDeeplinkHandler(
            deeplinkParser = get(),
            entryActivity = MainActivity::class.java,
        )
    }

    single { NotificationNavigationCoordinator() }
    single { NotificationDeeplinkNavigator() }
}
