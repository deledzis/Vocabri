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
package com.vocabri.data.di

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.vocabri.data.datasource.debug.DebugDataSource
import com.vocabri.data.datasource.debug.DebugDataSourceImpl
import com.vocabri.data.datasource.word.WordDataSource
import com.vocabri.data.datasource.word.WordLocalDataSourceImpl
import com.vocabri.data.db.VocabriDatabase
import com.vocabri.data.repository.debug.DebugRepositoryImpl
import com.vocabri.data.repository.word.WordRepositoryImpl
import com.vocabri.data.util.UuidIdGenerator
import com.vocabri.domain.model.kover.ExcludeFromCoverage
import com.vocabri.domain.repository.DebugRepository
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
    singleOf(::DebugDataSourceImpl) { bind<DebugDataSource>() }
    singleOf(::WordRepositoryImpl) { bind<WordRepository>() }
    singleOf(::DebugRepositoryImpl) { bind<DebugRepository>() }

    singleOf(::UuidIdGenerator) { bind<IdGenerator>() }
}
