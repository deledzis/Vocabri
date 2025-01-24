package com.vocabri.test.di

import com.vocabri.data.VocabriDatabase
import com.vocabri.data.test.FakeVocabriDatabase.createTestJdbcSqlDriver
import org.koin.dsl.module

val testModule = module {
    single<VocabriDatabase> {
        VocabriDatabase(
            driver = createTestJdbcSqlDriver(),
        )
    }
}
