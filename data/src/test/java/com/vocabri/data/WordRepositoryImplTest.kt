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

import com.vocabri.data.datasource.sync.PendingOperationDataSource
import com.vocabri.data.datasource.word.RemoteWordDataSource
import com.vocabri.data.datasource.word.WordDataSource
import com.vocabri.data.repository.word.WordRepositoryImpl
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.WordGender
import com.vocabri.utils.IdGenerator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class WordRepositoryImplTest {

    private lateinit var repository: WordRepositoryImpl
    private val localDataSource: WordDataSource = mockk(relaxed = true)
    private val remoteDataSource: RemoteWordDataSource = mockk(relaxed = true)
    private val pendingOperationDataSource: PendingOperationDataSource = mockk(relaxed = true)
    private val idGenerator: IdGenerator = mockk(relaxed = true)

    @Before
    fun setup() {
        every { idGenerator.generateStringId() } returns "test-id"
        repository = WordRepositoryImpl(
            localDataSource,
            remoteDataSource,
            pendingOperationDataSource,
            idGenerator,
        )
    }

    @Test
    fun `insertWord saves word to localDataSource`() = runTest {
        val testWord = Word.Verb(
            id = "1",
            text = "lernen",
            translations = emptyList(),
            examples = emptyList(),
            conjugation = "regular",
            management = "auf + Akk.",
        )

        coEvery { localDataSource.getWordsByPartOfSpeech(PartOfSpeech.VERB) } returns emptyList()
        coEvery { localDataSource.insertWord(testWord) } returns Unit
        coEvery { remoteDataSource.insertWord(testWord) } returns Unit

        // -- When --
        repository.insertWord(testWord)

        // -- Then --
        coVerify(exactly = 1) { remoteDataSource.insertWord(testWord) }
        coVerify(exactly = 1) { localDataSource.insertWord(testWord) }
        coVerify(exactly = 1) { localDataSource.getWordsByPartOfSpeech(PartOfSpeech.VERB) }
        coVerify(exactly = 0) { pendingOperationDataSource.insertPendingOperation(any()) }
        confirmVerified(localDataSource, remoteDataSource, pendingOperationDataSource)
    }

    @Test
    fun `getWords retrieves words from localDataSource`() = runTest {
        // -- Given --
        val storedWords = listOf(
            Word.Verb(
                id = "1",
                text = "lernen",
                translations = emptyList(),
                examples = emptyList(),
                conjugation = "regular",
                management = "auf + Akk.",
            ),
            Word.Noun(
                id = "2",
                text = "Haus",
                translations = emptyList(),
                examples = emptyList(),
                gender = WordGender.NEUTER,
                pluralForm = "Häuser",
            ),
        )

        coEvery { localDataSource.getWordsByPartOfSpeech(partOfSpeech = PartOfSpeech.ALL) } returns storedWords

        // -- When --
        val result = repository.getWordsByPartOfSpeech(partOfSpeech = PartOfSpeech.ALL)

        // -- Then --
        coVerify { localDataSource.getWordsByPartOfSpeech(partOfSpeech = PartOfSpeech.ALL) }
        assertEquals(2, result.size)
        assertEquals("lernen", result[0].text)
        assertEquals("Haus", result[1].text)
        confirmVerified(localDataSource, remoteDataSource, pendingOperationDataSource)
    }

    @Test
    fun `deleteWord deletes word from localDataSource`() = runTest {
        // -- Given --
        val wordId = "123"
        coEvery { localDataSource.deleteWord(wordId) } returns Unit
        coEvery { remoteDataSource.deleteWord(wordId) } returns Unit

        // -- When --
        repository.deleteWordById(wordId)

        // -- Then --
        coVerify { remoteDataSource.deleteWord(wordId) }
        coVerify { localDataSource.deleteWord(wordId) }
        coVerify(exactly = 0) { pendingOperationDataSource.insertPendingOperation(any()) }
        confirmVerified(localDataSource, remoteDataSource, pendingOperationDataSource)
    }

    @Test
    fun `insertWord throws exception for duplicate word`() = runTest {
        // -- Given --
        val newWord = Word.Verb(
            id = "1",
            text = "lernen",
            translations = emptyList(),
            examples = emptyList(),
            conjugation = "regular",
            management = "auf + Akk.",
        )

        val existingWord = Word.Verb(
            id = "2",
            text = "lernen",
            translations = emptyList(),
            examples = emptyList(),
            conjugation = "regular",
            management = "auf + Akk.",
        )
        coEvery { localDataSource.getWordsByPartOfSpeech(PartOfSpeech.VERB) } returns listOf(existingWord)

        // -- When & Then --
        assertThrows(
            "Word with text 'lernen' already exists",
            IllegalStateException::class.java,
        ) {
            runBlocking {
                repository.insertWord(newWord)
            }
        }
        coVerify(exactly = 0) {
            remoteDataSource.insertWord(newWord)
        }
        coVerify(exactly = 0) {
            localDataSource.insertWord(newWord)
        }
        coVerify(exactly = 1) { localDataSource.getWordsByPartOfSpeech(PartOfSpeech.VERB) }
        coVerify(exactly = 0) { pendingOperationDataSource.insertPendingOperation(any()) }
        confirmVerified(localDataSource, remoteDataSource, pendingOperationDataSource)
    }
}
