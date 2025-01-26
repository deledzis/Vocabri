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

import com.vocabri.data.datasource.word.WordDataSource
import com.vocabri.data.repository.word.WordRepositoryImpl
import com.vocabri.data.test.FakeIdGenerator
import com.vocabri.domain.model.word.Example
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class WordRepositoryImplTest {

    private lateinit var repository: WordRepositoryImpl
    private val localDataSource: WordDataSource = mockk(relaxed = true)
    private val idGenerator = FakeIdGenerator()

    @Before
    fun setup() {
        repository = WordRepositoryImpl(localDataSource, idGenerator)
    }

    @Test
    fun `insertWord saves word to localDataSource`() = runBlocking {
        val word = Word(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("1", "learn")),
            examples = listOf(Example("1", "Ich lerne")),
            partOfSpeech = PartOfSpeech.VERB,
            notes = null,
        )

        coEvery { localDataSource.insertWord(any()) } returns Unit
        coEvery { localDataSource.insertTranslation(any()) } returns Unit
        coEvery { localDataSource.insertExample(any()) } returns Unit

        repository.insertWord(word)

        coVerify {
            localDataSource.insertWord(
                WordEntity(
                    id = "1",
                    text = "lernen",
                    partOfSpeech = "VERB",
                    notes = null,
                ),
            )
            localDataSource.insertTranslation(
                TranslationEntity(
                    id = idGenerator.id,
                    wordId = "1",
                    translation = "learn",
                ),
            )
            localDataSource.insertExample(
                ExampleEntity(
                    id = idGenerator.id,
                    wordId = "1",
                    example = "Ich lerne",
                ),
            )
        }
    }

    @Test
    fun `getWords retrieves words from localDataSource`() = runBlocking {
        val dbWords = listOf(
            WordEntity(
                id = "1",
                text = "lernen",
                partOfSpeech = "VERB",
                notes = null,
            ),
        )
        val dbTranslations = listOf(
            TranslationEntity(
                id = "1",
                wordId = "1",
                translation = "learn",
            ),
        )
        val dbExamples = listOf(
            ExampleEntity(
                id = "1",
                wordId = "1",
                example = "Ich lerne",
            ),
        )

        coEvery { localDataSource.getAllWords() } returns dbWords
        coEvery { localDataSource.getTranslationsByWordId(any()) } returns dbTranslations
        coEvery { localDataSource.getExamplesByWordId(any()) } returns dbExamples

        val expectedDomainWords = listOf(
            Word(
                id = "1",
                text = "lernen",
                translations = listOf(Translation("1", "learn")),
                examples = listOf(Example("1", "Ich lerne")),
                partOfSpeech = PartOfSpeech.VERB,
                notes = null,
            ),
        )

        val result = repository.getAllWords()
        assertEquals(expectedDomainWords, result)
    }

    @Test
    fun `deleteWord deletes word and related data from localDataSource`() = runBlocking {
        val wordId = "1"

        coEvery { localDataSource.deleteWordById(wordId) } returns Unit
        coEvery { localDataSource.deleteTranslationsByWordId(wordId) } returns Unit
        coEvery { localDataSource.deleteExamplesByWordId(wordId) } returns Unit

        repository.deleteWordById(wordId)

        coVerifyOrder {
            localDataSource.deleteWordById(wordId)
            localDataSource.deleteTranslationsByWordId(wordId)
            localDataSource.deleteExamplesByWordId(wordId)
        }
    }

    @Test
    fun `saveWord throws exception for duplicate word`(): Unit = runBlocking {
        val word = Word(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("1", "learn")),
            examples = listOf(Example("1", "Ich lerne")),
            partOfSpeech = PartOfSpeech.VERB,
            notes = "Irregular verb",
        )

        val existingWordEntity = WordEntity(
            id = "2",
            text = "lernen",
            partOfSpeech = "VERB",
            notes = null,
        )

        coEvery { localDataSource.getAllWords() } returns listOf(existingWordEntity)

        assertThrows(
            "Word with text 'lernen' already exists",
            IllegalArgumentException::class.java,
        ) {
            runBlocking {
                repository.insertWord(word)
            }
        }
    }
}
