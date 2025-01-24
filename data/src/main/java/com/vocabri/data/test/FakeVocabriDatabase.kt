package com.vocabri.data.test

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.vocabri.data.VocabriDatabase

object FakeVocabriDatabase {
    fun createTestJdbcSqlDriver(): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        VocabriDatabase.Schema.synchronous().create(this)
    }
}
