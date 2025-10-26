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
package com.vocabri

import android.app.Application
import com.vocabri.data.di.dataModule
import com.vocabri.di.appModule
import com.vocabri.domain.di.domainModule
import com.vocabri.logger.logger
import com.vocabri.notifications.PushNotificationInitializer
import com.vocabri.notifications.PushNotificationServiceLocator
import com.vocabri.notifications.PushNotificationTokenRepository
import com.vocabri.notifications.di.notificationsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.get
import org.koin.core.context.startKoin

class VocabriApplication : Application() {
    private val log = logger()

    override fun onCreate() {
        super.onCreate()

        log.i { "VocabriApplication is starting" }

        // Initialize Koin
        val koinApplication = startKoin {
            log.i { "Initializing Koin with application context and modules" }
            androidLogger()
            androidContext(this@VocabriApplication)
            modules(
                dataModule,
                domainModule,
                appModule,
                notificationsModule,
            )
        }
        log.i { "Koin initialization completed" }

        val koin = koinApplication.koin
        val tokenRepository = koin.get<PushNotificationTokenRepository>()
        PushNotificationServiceLocator.register(tokenRepository)
        koin.get<PushNotificationInitializer>().initialize()
    }

    override fun onTerminate() {
        super.onTerminate()
        PushNotificationServiceLocator.unregister()
    }
}
