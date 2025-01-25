package com.vocabri.test.di

import com.vocabri.data.db.VocabriDatabase
import com.vocabri.data.test.FakeVocabriDatabase.createTestJdbcSqlDriver
import com.vocabri.domain.model.kover.ExcludeFromCoverage
import org.koin.dsl.module

@ExcludeFromCoverage
val testModule = module {
    single<VocabriDatabase> {
        VocabriDatabase(
            driver = createTestJdbcSqlDriver(),
        )
    }
}
