package com.vocabri

import android.app.Application
import com.vocabri.data.di.dataModule
import com.vocabri.di.appModule
import com.vocabri.domain.di.domainModule
import com.vocabri.domain.util.logger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class VocabriApplication : Application() {
    private val log = logger()

    override fun onCreate() {
        super.onCreate()

        log.i { "VocabriApplication is starting" }

        // Initialize Koin
        startKoin {
            log.i { "Initializing Koin with application context and modules" }
            androidLogger()
            androidContext(this@VocabriApplication)
            modules(
                dataModule,
                domainModule,
                appModule
            )
        }
        log.i { "Koin initialization completed" }
    }
}
