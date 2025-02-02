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
package com.vocabri.ui.dictionarydetails

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.WordGender
import com.vocabri.domain.repository.WordRepository
import com.vocabri.domain.usecase.word.DeleteWordUseCase
import com.vocabri.domain.usecase.word.GetWordsUseCase
import com.vocabri.rules.MainDispatcherRule
import com.vocabri.ui.dictionary.model.toTitleResId
import com.vocabri.ui.dictionarydetails.viewmodel.DictionaryDetailsEvent
import com.vocabri.ui.dictionarydetails.viewmodel.DictionaryDetailsState
import com.vocabri.ui.dictionarydetails.viewmodel.DictionaryDetailsViewModel
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryDetailsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var mockWordRepository: WordRepository
    private lateinit var getWordsUseCase: GetWordsUseCase
    private lateinit var deleteWordUseCase: DeleteWordUseCase
    private lateinit var viewModel: DictionaryDetailsViewModel

    private val samplePartOfSpeech = PartOfSpeech.VERB

    @Before
    fun setup() {
        mockWordRepository = mockk(relaxed = true)
        getWordsUseCase = GetWordsUseCase(mockWordRepository)
        deleteWordUseCase = DeleteWordUseCase(mockWordRepository)
        viewModel = DictionaryDetailsViewModel(
            getWordsUseCase = getWordsUseCase,
            deleteWordUseCase = deleteWordUseCase,
            ioScope = TestScope(dispatcherRule.testDispatcher),
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `LoadWordsByGroup transitions state to WordsLoaded on success`() = runTest {
        val sampleWords = listOf(
            Word.Verb(
                id = "1",
                text = "lernen",
                translations = listOf(
                    Translation(id = "1", translation = "learn"),
                    Translation(id = "2", translation = "study"),
                ),
                examples = emptyList(),
                conjugation = "regular",
                tenseForms = "present",
            ),
            Word.Noun(
                id = "2",
                text = "Haus",
                translations = listOf(
                    Translation(id = "3", translation = "house"),
                ),
                examples = emptyList(),
                gender = WordGender.NEUTER,
                pluralForm = "Häuser",
            ),
        )

        coEvery { mockWordRepository.getWordsByPartOfSpeech(samplePartOfSpeech) } returns sampleWords.take(1)

        viewModel.handleEvent(DictionaryDetailsEvent.LoadWords(wordGroup = samplePartOfSpeech.name))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue(state is DictionaryDetailsState.WordsLoaded)

        state as DictionaryDetailsState.WordsLoaded
        assertEquals(1, state.words.size)
        assertEquals("lernen", state.words[0].text)

        coVerify(exactly = 1) { mockWordRepository.getWordsByPartOfSpeech(samplePartOfSpeech) }
    }

    @Test
    fun `LoadWordsByGroup handles error gracefully`() = runTest {
        val errorMessage = "Error loading words"
        coEvery { getWordsUseCase.executeByPartOfSpeech(samplePartOfSpeech) } throws Exception(errorMessage)

        viewModel.handleEvent(DictionaryDetailsEvent.LoadWords(wordGroup = samplePartOfSpeech.name))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(
            DictionaryDetailsState.Error(
                titleId = samplePartOfSpeech.toTitleResId,
                message = errorMessage,
            ),
            state,
        )
        coVerify(exactly = 1) { getWordsUseCase.executeByPartOfSpeech(samplePartOfSpeech) }
    }

    @Test
    fun `LoadWordsByGroup handles empty list gracefully`() = runTest {
        coEvery { mockWordRepository.getWordsByPartOfSpeech(samplePartOfSpeech) } returns emptyList()

        viewModel.handleEvent(DictionaryDetailsEvent.LoadWords(wordGroup = samplePartOfSpeech.name))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(
            DictionaryDetailsState.Empty(titleId = samplePartOfSpeech.toTitleResId),
            state,
        )
        coVerify(exactly = 1) { mockWordRepository.getWordsByPartOfSpeech(samplePartOfSpeech) }
    }

    @Test
    fun `DeleteWord deletes word and reloads group`() = runTest {
        val sampleWords = listOf(
            Word.Verb(
                id = "1",
                text = "lernen",
                translations = listOf(
                    Translation(id = "1", translation = "learn"),
                ),
                examples = emptyList(),
                conjugation = "regular",
                tenseForms = "present",
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

        coEvery { mockWordRepository.getWordsByPartOfSpeech(samplePartOfSpeech) } returns sampleWords.take(1)
        coEvery { mockWordRepository.deleteWordById("1") } just Runs

        viewModel.handleEvent(DictionaryDetailsEvent.LoadWords(wordGroup = samplePartOfSpeech.name))
        viewModel.handleEvent(DictionaryDetailsEvent.DeleteWordClicked("1"))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue(state is DictionaryDetailsState.WordsLoaded)
        coVerify { mockWordRepository.deleteWordById("1") }
        coVerify(exactly = 2) { mockWordRepository.getWordsByPartOfSpeech(samplePartOfSpeech) }
    }

    @Test
    fun `LoadWordsByGroup prevents duplicate calls`() = runTest {
        coEvery { mockWordRepository.getWordsByPartOfSpeech(samplePartOfSpeech) } returns emptyList()

        viewModel.handleEvent(DictionaryDetailsEvent.LoadWords(wordGroup = samplePartOfSpeech.name))
        viewModel.handleEvent(DictionaryDetailsEvent.LoadWords(wordGroup = samplePartOfSpeech.name))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(
            DictionaryDetailsState.Empty(titleId = samplePartOfSpeech.toTitleResId),
            state,
        )
        coVerify(exactly = 1) { mockWordRepository.getWordsByPartOfSpeech(samplePartOfSpeech) }
    }
}
