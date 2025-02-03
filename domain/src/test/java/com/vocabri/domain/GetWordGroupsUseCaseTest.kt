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

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.WordGender
import com.vocabri.domain.repository.WordRepository
import com.vocabri.domain.usecase.word.GetWordGroupsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWordGroupsUseCaseTest {

    private lateinit var repository: WordRepository
    private lateinit var getWordGroupsUseCase: GetWordGroupsUseCase

    @Before
    fun setup() {
        repository = mockk()
        getWordGroupsUseCase = GetWordGroupsUseCase(repository)
    }

    @Test
    fun `execute returns correct group counts`() = runTest {
        val word1 = Word.Verb(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("t1", "learn")),
            examples = emptyList(),
            conjugation = "regular",
            management = "auf + Akk.",
        )
        val word2 = Word.Noun(
            id = "2",
            text = "Haus",
            translations = listOf(Translation("t2", "house")),
            examples = emptyList(),
            gender = WordGender.NEUTER,
            pluralForm = "Häuser",
        )
        val word3 = Word.Adverb(
            id = "3",
            text = "schnell",
            translations = listOf(Translation("t3", "quickly")),
            examples = emptyList(),
            comparative = null,
            superlative = null,
        )

        coEvery { repository.getAllWords() } returns listOf(word1, word2, word3)

        val result = getWordGroupsUseCase.execute()

        assertEquals(3, result.allWords.wordCount)

        val sortedGroups = result.groups.sortedBy { it.partOfSpeech.name }

        assertEquals(3, sortedGroups.size)

        assertEquals(PartOfSpeech.ADVERB, sortedGroups[0].partOfSpeech)
        assertEquals(1, sortedGroups[0].wordCount)

        assertEquals(PartOfSpeech.NOUN, sortedGroups[1].partOfSpeech)
        assertEquals(1, sortedGroups[1].wordCount)

        assertEquals(PartOfSpeech.VERB, sortedGroups[2].partOfSpeech)
        assertEquals(1, sortedGroups[2].wordCount)
    }

    @Test
    fun `execute returns empty group if no words exist`() = runTest {
        coEvery { repository.getAllWords() } returns emptyList()

        val result = getWordGroupsUseCase.execute()

        assertEquals(0, result.allWords.wordCount)
        assertEquals(0, result.groups.size)
    }
}
