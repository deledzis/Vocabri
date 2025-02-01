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
import com.vocabri.domain.model.word.Example
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.toPartOfSpeech
import com.vocabri.domain.repository.WordRepository
import com.vocabri.domain.usecase.word.AddWordUseCase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class AddWordUseCaseTest {

    private lateinit var fakeRepository: WordRepository
    private lateinit var addWordUseCase: AddWordUseCase

    @Before
    fun setup() {
        fakeRepository = FakeWordRepository()
        addWordUseCase = AddWordUseCase(fakeRepository)
    }

    @Test
    fun `execute adds word with details to repository`() = runTest {
        val word = Word.Verb(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("1", "learn")),
            examples = listOf(),
            conjugation = "irregular",
            tenseForms = "present, past, perfect",
        )

        addWordUseCase.execute(word)

        val words = fakeRepository.getAllWords()
        assertEquals(1, words.size)

        val addedWord = words[0]
        assertEquals("lernen", addedWord.text)
        assertEquals(PartOfSpeech.VERB, addedWord.toPartOfSpeech())
        assertEquals(1, addedWord.translations.size)
        assertEquals("learn", addedWord.translations[0].translation)
    }

    @Test
    fun `getWords retrieves all words with details from repository`() = runTest {
        val word1 = Word.Verb(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("1", "learn")),
            examples = listOf(Example("1", "Ich lerne")),
            conjugation = "irregular",
            tenseForms = "present, past, perfect",
        )
        val word2 = Word.Noun(
            id = "2",
            text = "Haus",
            translations = listOf(Translation("2", "house")),
            examples = listOf(Example("2", "Das ist mein Haus")),
            gender = "das",
            pluralForm = "Häuser",
        )

        fakeRepository.insertWord(word1)
        fakeRepository.insertWord(word2)

        val words = fakeRepository.getAllWords()
        assertEquals(2, words.size)

        val retrievedWord1 = words[0]
        assertEquals("lernen", retrievedWord1.text)
        assertEquals(PartOfSpeech.VERB, retrievedWord1.toPartOfSpeech())
        assertEquals(1, retrievedWord1.translations.size)
        assertEquals("learn", retrievedWord1.translations[0].translation)
        assertEquals(1, retrievedWord1.examples.size)
        assertEquals("Ich lerne", retrievedWord1.examples[0].example)

        val retrievedWord2 = words[1]
        assertEquals("Haus", retrievedWord2.text)
        assertEquals(PartOfSpeech.NOUN, retrievedWord2.toPartOfSpeech())
        assertEquals(1, retrievedWord2.translations.size)
        assertEquals("house", retrievedWord2.translations[0].translation)
        assertEquals(1, retrievedWord2.examples.size)
        assertEquals("Das ist mein Haus", retrievedWord2.examples[0].example)
    }

    @Test
    fun `execute does not add duplicate words to repository`() = runTest {
        val word = Word.Verb(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("1", "learn")),
            examples = listOf(),
            conjugation = "irregular",
            tenseForms = "present, past, perfect",
        )

        addWordUseCase.execute(word)

        assertThrows(
            "Word with text 'lernen' already exists",
            IllegalArgumentException::class.java,
        ) {
            runBlocking { addWordUseCase.execute(word) }
        }

        val words = fakeRepository.getAllWords()
        assertEquals(1, words.size)
    }
}
