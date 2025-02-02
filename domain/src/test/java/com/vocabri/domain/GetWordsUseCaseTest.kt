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
package com.vocabri.domain

import com.vocabri.domain.fake.FakeWordRepositoryImpl
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.WordGender
import com.vocabri.domain.repository.WordRepository
import com.vocabri.domain.usecase.word.GetWordsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWordsUseCaseTest {

    private lateinit var fakeRepository: WordRepository
    private lateinit var getWordsUseCase: GetWordsUseCase

    @Before
    fun setup() {
        fakeRepository = FakeWordRepositoryImpl()
        getWordsUseCase = GetWordsUseCase(fakeRepository)
    }

    @Test
    fun `execute retrieves words from repository`() = runTest {
        val word1 = Word.Verb(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("1", "learn")),
            examples = listOf(),
            conjugation = "irregular",
            tenseForms = "present, past, perfect",
        )
        val word2 = Word.Noun(
            id = "2",
            text = "Haus",
            translations = listOf(Translation("2", "house")),
            examples = listOf(),
            gender = WordGender.NEUTER,
            pluralForm = "Häuser",
        )

        fakeRepository.insertWord(word1)
        fakeRepository.insertWord(word2)

        val words = getWordsUseCase.execute()

        assertEquals(2, words.size)
        assertEquals("lernen", words[0].text)
        assertEquals("Haus", words[1].text)
    }
}
