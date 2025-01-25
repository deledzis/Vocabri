package com.vocabri.data

import com.vocabri.data.datasource.word.WordLocalDataSourceImpl
import com.vocabri.data.db.VocabriDatabase
import com.vocabri.data.test.FakeVocabriDatabase.createTestJdbcSqlDriver
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WordLocalDataSourceImplTest {

    private val driver = createTestJdbcSqlDriver()
    private val database = VocabriDatabase(driver)

    private lateinit var dataSource: WordLocalDataSourceImpl

    @Before
    fun setup() {
        dataSource = WordLocalDataSourceImpl(database = database)
    }

    @After
    fun tearDown() {
        driver.close()
    }

    @Test
    fun `addWord adds word and its details to database`() = runTest {
        val word = WordEntity(
            id = "1",
            text = "lernen",
            partOfSpeech = "VERB",
            notes = "Some notes",
        )
        val translation = TranslationEntity(
            id = "1",
            wordId = "1",
            translation = "learn",
        )
        val example = ExampleEntity(
            id = "1",
            wordId = "1",
            example = "Ich lerne",
        )

        dataSource.insertWord(word)
        dataSource.insertTranslation(translation)
        dataSource.insertExample(example)

        val resultWord = dataSource.getWordById("1")
        val resultTranslations = dataSource.getTranslationsByWordId("1")
        val resultExamples = dataSource.getExamplesByWordId("1")

        assertEquals(word, resultWord)
        assertEquals(listOf(translation), resultTranslations)
        assertEquals(listOf(example), resultExamples)
    }

    @Test
    fun `getWords retrieves all words with translations and examples`() = runTest {
        val word1 = WordEntity(
            id = "1",
            text = "lernen",
            partOfSpeech = "VERB",
            notes = null,
        )
        val word2 = WordEntity(
            id = "2",
            text = "Haus",
            partOfSpeech = "NOUN",
            notes = "Important",
        )
        val translation1 = TranslationEntity(id = "1", wordId = "1", translation = "learn")
        val translation2 = TranslationEntity(id = "2", wordId = "2", translation = "house")
        val example1 = ExampleEntity(id = "1", wordId = "1", example = "Ich lerne")
        val example2 = ExampleEntity(id = "2", wordId = "2", example = "Das ist mein Haus")

        dataSource.insertWord(word1)
        dataSource.insertWord(word2)
        dataSource.insertTranslation(translation1)
        dataSource.insertTranslation(translation2)
        dataSource.insertExample(example1)
        dataSource.insertExample(example2)

        val results = dataSource.getAllWords()
        assertEquals(2, results.size)
        assertEquals(word1, results[0])
        assertEquals(word2, results[1])

        val translations1 = dataSource.getTranslationsByWordId("1")
        val translations2 = dataSource.getTranslationsByWordId("2")
        val examples1 = dataSource.getExamplesByWordId("1")
        val examples2 = dataSource.getExamplesByWordId("2")

        assertEquals(listOf(translation1), translations1)
        assertEquals(listOf(translation2), translations2)
        assertEquals(listOf(example1), examples1)
        assertEquals(listOf(example2), examples2)
    }

    @Test
    fun `getWordById returns null and empty details for non-existent word`() = runTest {
        val resultWord = dataSource.getWordById("non-existent-id")
        val resultTranslations = dataSource.getTranslationsByWordId("non-existent-id")
        val resultExamples = dataSource.getExamplesByWordId("non-existent-id")

        assertNull(resultWord)
        assertTrue(resultTranslations.isEmpty())
        assertTrue(resultExamples.isEmpty())
    }
}
