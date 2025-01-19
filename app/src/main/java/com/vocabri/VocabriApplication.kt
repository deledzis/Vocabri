package com.vocabri

import android.app.Application
import com.vocabri.data.di.dataModule
import com.vocabri.di.appModule
import com.vocabri.domain.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class VocabriApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@VocabriApplication)
            modules(
                dataModule,
                domainModule,
                appModule
            )
        }
    }
}
