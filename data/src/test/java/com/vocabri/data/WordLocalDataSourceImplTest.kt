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
package com.vocabri.data

import com.vocabri.data.datasource.word.WordLocalDataSourceImpl
import com.vocabri.data.db.VocabriDatabase
import com.vocabri.data.test.FakeVocabriDatabase.createTestJdbcSqlDriver
import com.vocabri.domain.model.word.Example
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.WordGender
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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

    private suspend fun WordLocalDataSourceImpl.getWordByIdForTest(id: String): Word? =
        getWordsByPartOfSpeech(partOfSpeech = PartOfSpeech.ALL).find { it.id == id }

    @Test
    fun `addWord adds word and its details to database`() = runTest {
        val word = Word.Verb(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("t1", "learn")),
            examples = listOf(Example("e1", "Ich lerne")),
            conjugation = "regular",
            management = "auf + Akk.",
        )

        dataSource.insertWord(word)

        val resultWord = dataSource.getWordByIdForTest("1")
        assertNotNull(resultWord)
        assertTrue(resultWord is Word.Verb)

        val verbWord = resultWord as Word.Verb
        assertEquals("lernen", verbWord.text)
        assertEquals("regular", verbWord.conjugation)
        assertEquals("auf + Akk.", verbWord.management)

        assertEquals(1, verbWord.translations.size)
        assertEquals("learn", verbWord.translations[0].translation)

        assertEquals(1, verbWord.examples.size)
        assertEquals("Ich lerne", verbWord.examples[0].example)
    }

    @Test
    fun `getWords retrieves all words with translations and examples`() = runTest {
        val word1 = Word.Verb(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("t1", "learn")),
            examples = listOf(Example("e1", "Ich lerne")),
            conjugation = "regular",
            management = "auf + Akk.",
        )
        val word2 = Word.Noun(
            id = "2",
            text = "Haus",
            translations = listOf(Translation("t2", "house")),
            examples = listOf(Example("e2", "Das ist mein Haus")),
            gender = WordGender.NEUTER,
            pluralForm = "Häuser",
        )

        dataSource.insertWord(word1)
        dataSource.insertWord(word2)

        val results = dataSource.getWordsByPartOfSpeech(partOfSpeech = PartOfSpeech.ALL)

        assertEquals(2, results.size)

        val sorted = results.sortedBy { it.id }

        val verbWord = sorted[0] as Word.Verb
        assertEquals("1", verbWord.id)
        assertEquals("lernen", verbWord.text)
        assertEquals(1, verbWord.translations.size)
        assertEquals("learn", verbWord.translations[0].translation)
        assertEquals(1, verbWord.examples.size)
        assertEquals("Ich lerne", verbWord.examples[0].example)

        val nounWord = sorted[1] as Word.Noun
        assertEquals("2", nounWord.id)
        assertEquals("Haus", nounWord.text)
        assertEquals(WordGender.NEUTER, nounWord.gender)
        assertEquals("Häuser", nounWord.pluralForm)
        assertEquals(1, nounWord.translations.size)
        assertEquals("house", nounWord.translations[0].translation)
        assertEquals(1, nounWord.examples.size)
        assertEquals("Das ist mein Haus", nounWord.examples[0].example)
    }

    @Test
    fun `getWordById returns null for non-existent word`() = runTest {
        val resultWord = dataSource.getWordByIdForTest("non-existent-id")
        assertNull(resultWord)

        val allWords = dataSource.getWordsByPartOfSpeech(partOfSpeech = PartOfSpeech.ALL)
        assertTrue(allWords.isEmpty())
    }
}
