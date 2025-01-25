package com.vocabri.data.di

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.vocabri.data.datasource.word.WordDataSource
import com.vocabri.data.datasource.word.WordLocalDataSourceImpl
import com.vocabri.data.db.VocabriDatabase
import com.vocabri.data.repository.word.WordRepositoryImpl
import com.vocabri.data.util.TimeBasedIdGenerator
import com.vocabri.domain.model.kover.ExcludeFromCoverage
import com.vocabri.domain.repository.WordRepository
import com.vocabri.utils.IdGenerator
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@ExcludeFromCoverage
val dataModule = module {
    single {
        VocabriDatabase(
            driver = AndroidSqliteDriver(
                schema = VocabriDatabase.Schema.synchronous(),
                context = androidContext(),
                name = "vocabri.db",
            ),
        )
    }

    singleOf(::WordLocalDataSourceImpl) { bind<WordDataSource>() }
    singleOf(::WordRepositoryImpl) { bind<WordRepository>() }

    singleOf(::TimeBasedIdGenerator) { bind<IdGenerator>() }
}
