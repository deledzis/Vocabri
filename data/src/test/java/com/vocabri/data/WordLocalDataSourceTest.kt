package com.vocabri.data

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.vocabri.data.datasource.word.WordLocalDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WordLocalDataSourceTest {

    private val driver = createTestSqlDriver()
    private val database = VocabriDatabase(driver)
    private val dataSource: WordLocalDataSource = WordLocalDataSource(database)

    private fun createTestSqlDriver(): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            VocabriDatabase.Schema.synchronous().create(this)
        }
    }

    @Test
    fun `addWord adds word to database`() = runTest {
        val word = WordEntity(
            id = "1",
            text = "lernen",
            translations = "learn, study",
            examples = "Ich lerne,Du lernst",
            partOfSpeech = "VERB",
            notes = "Some notes"
        )
        dataSource.addWord(word)

        val result = dataSource.getWordById("1")
        assertEquals(word, result)
    }

    @Test
    fun `getWords retrieves all words`() = runTest {
        val word1 = WordEntity(
            id = "1",
            text = "lernen",
            translations = "learn",
            examples = "Ich lerne",
            partOfSpeech = "VERB",
            notes = null
        )
        val word2 = WordEntity(
            id = "2",
            text = "Haus",
            translations = "house",
            examples = "Das Haus ist groß",
            partOfSpeech = "noun",
            notes = "Important"
        )
        dataSource.addWord(word1)
        dataSource.addWord(word2)

        val results = dataSource.getWords()
        assertEquals(2, results.size)
        assertEquals(word1, results[0])
        assertEquals(word2, results[1])
    }

    @Test
    fun `getWordById returns null for non-existent word`() = runTest {
        val result = dataSource.getWordById("non-existent-id")
        assertNull(result)
    }
}