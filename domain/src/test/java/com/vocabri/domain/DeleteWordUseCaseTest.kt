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

import com.vocabri.domain.fake.FakeWordRepository
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.usecase.word.DeleteWordUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeleteWordUseCaseTest {

    private lateinit var useCase: DeleteWordUseCase
    private lateinit var fakeRepository: FakeWordRepository

    @Before
    fun setup() {
        fakeRepository = FakeWordRepository()
        useCase = DeleteWordUseCase(fakeRepository)
    }

    @Test
    fun `deleteWord removes the word from repository`() = runBlocking {
        val word1 = Word.Verb(
            id = "1",
            text = "lernen",
            translations = emptyList(),
            examples = emptyList(),
            conjugation = "irregular",
            tenseForms = "present",
        )
        val word2 = Word.Noun(
            id = "2",
            text = "Haus",
            translations = emptyList(),
            examples = emptyList(),
            gender = "das",
            pluralForm = "Häuser",
        )
        fakeRepository.insertWord(word1)
        fakeRepository.insertWord(word2)

        // Act
        useCase.execute(word1.id)

        // Assert
        val remainingWords = fakeRepository.getAllWords()
        assertEquals(1, remainingWords.size)
        assertEquals(word2, remainingWords.first())
    }

    @Test
    fun `deleteWord does nothing if word does not exist`() = runBlocking {
        val word = Word.Verb(
            id = "1",
            text = "lernen",
            translations = emptyList(),
            examples = emptyList(),
            conjugation = "irregular",
            tenseForms = "present",
        )
        fakeRepository.insertWord(word)

        // Act
        useCase.execute("non-existent-id")

        // Assert
        val remainingWords = fakeRepository.getAllWords()
        assertEquals(1, remainingWords.size)
        assertEquals(word, remainingWords.first())
    }

    @Test
    fun `deleteWord allows deleting multiple words`() = runBlocking {
        val word1 = Word.Verb(
            id = "1",
            text = "lernen",
            translations = emptyList(),
            examples = emptyList(),
            conjugation = "irregular",
            tenseForms = "present",
        )
        val word2 = Word.Noun(
            id = "2",
            text = "Haus",
            translations = emptyList(),
            examples = emptyList(),
            gender = "das",
            pluralForm = "Häuser",
        )
        fakeRepository.insertWord(word1)
        fakeRepository.insertWord(word2)

        // Act
        useCase.execute(word1.id)
        useCase.execute(word2.id)

        // Assert
        val remainingWords = fakeRepository.getAllWords()
        assertEquals(0, remainingWords.size)
    }
}
