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
import com.vocabri.domain.usecase.word.ObserveWordsUseCase
import com.vocabri.rules.MainDispatcherRule
import com.vocabri.ui.screens.dictionary.model.toTitleResId
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsEvent
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsState
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsViewModel
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryDetailsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var mockWordRepository: WordRepository
    private lateinit var observeWordsUseCase: ObserveWordsUseCase
    private lateinit var deleteWordUseCase: DeleteWordUseCase
    private lateinit var viewModel: DictionaryDetailsViewModel

    private val samplePartOfSpeech = PartOfSpeech.VERB

    private val sampleWords = listOf(
        Word.Verb(
            id = "1",
            text = "lernen",
            translations = listOf(
                Translation(id = "1", translation = "learn"),
                Translation(id = "2", translation = "study"),
            ),
            examples = emptyList(),
            conjugation = "regular",
            management = "auf + Akk.",
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

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `LoadWordsByGroup transitions state to WordsLoaded on success`() = runTest {
        setupViewModel()
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue(state is DictionaryDetailsState.WordsLoaded)

        state as DictionaryDetailsState.WordsLoaded
        assertEquals(1, state.words.size)
        assertEquals("lernen", state.words[0].text)

        coVerify(exactly = 1) { mockWordRepository.observeWordsByPartOfSpeech(samplePartOfSpeech) }
    }

    @Test
    fun `LoadWordsByGroup handles error gracefully`() = runTest {
        setupWordRepository()
        observeWordsUseCase = ObserveWordsUseCase(mockWordRepository)
        val errorMessage = "Error loading words"
        coEvery { observeWordsUseCase.executeByPartOfSpeech(samplePartOfSpeech) } throws Exception(errorMessage)
        setupViewModel(
            setupWordRepository = false,
            setupObserveWordsUseCase = false,
        )
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
        coVerify(exactly = 1) { observeWordsUseCase.executeByPartOfSpeech(samplePartOfSpeech) }
    }

    @Test
    fun `LoadWordsByGroup handles empty list gracefully`() = runTest {
        setupViewModel(words = emptyList())
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(
            DictionaryDetailsState.Empty(titleId = samplePartOfSpeech.toTitleResId),
            state,
        )
        coVerify(exactly = 1) { mockWordRepository.observeWordsByPartOfSpeech(samplePartOfSpeech) }
    }

    @Test
    fun `DeleteWord deletes word and reloads group`() = runTest {
        setupWordRepository()
        coEvery { mockWordRepository.deleteWordById("1") } just Runs
        setupViewModel(setupWordRepository = false)
        advanceUntilIdle()

        viewModel.handleEvent(DictionaryDetailsEvent.DeleteWordClicked("1"))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue(state is DictionaryDetailsState.WordsLoaded)
        coVerify { mockWordRepository.deleteWordById("1") }
        coVerify(exactly = 1) { mockWordRepository.observeWordsByPartOfSpeech(samplePartOfSpeech) }
    }

    @Test
    fun `LoadWordsByGroup prevents duplicate calls`() = runTest {
        setupViewModel(words = emptyList())
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertEquals(
            DictionaryDetailsState.Empty(titleId = samplePartOfSpeech.toTitleResId),
            state,
        )
        coVerify(exactly = 1) { mockWordRepository.observeWordsByPartOfSpeech(samplePartOfSpeech) }
    }

    private fun setupWordRepository(words: List<Word> = sampleWords.take(1)) {
        mockWordRepository = mockk(relaxed = true)
        coEvery { mockWordRepository.observeWordsByPartOfSpeech(any()) } returns flowOf(words)
    }

    private fun setupObserveWordsUseCase() {
        observeWordsUseCase = ObserveWordsUseCase(mockWordRepository)
    }

    private fun setupDeleteWordUseCase() {
        deleteWordUseCase = DeleteWordUseCase(mockWordRepository)
    }

    private fun setupViewModel(
        words: List<Word> = sampleWords.take(1),
        setupWordRepository: Boolean = true,
        setupObserveWordsUseCase: Boolean = true,
        setupDeleteWordUseCase: Boolean = true,
    ) {
        if (setupWordRepository) setupWordRepository(words)
        if (setupObserveWordsUseCase) setupObserveWordsUseCase()
        if (setupDeleteWordUseCase) setupDeleteWordUseCase()

        viewModel = DictionaryDetailsViewModel(
            partOfSpeech = samplePartOfSpeech,
            observeWordsUseCase = observeWordsUseCase,
            deleteWordUseCase = deleteWordUseCase,
            ioScope = TestScope(dispatcherRule.testDispatcher),
        )
    }
}
