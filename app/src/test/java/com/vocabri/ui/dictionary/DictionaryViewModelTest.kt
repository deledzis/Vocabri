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
package com.vocabri.ui.dictionary

import app.cash.turbine.test
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.usecase.word.DeleteWordUseCase
import com.vocabri.domain.usecase.word.GetWordsUseCase
import com.vocabri.rules.MainDispatcherRule
import com.vocabri.ui.dictionary.model.toUiModel
import com.vocabri.ui.dictionary.viewmodel.DictionaryEvent
import com.vocabri.ui.dictionary.viewmodel.DictionaryState
import com.vocabri.ui.dictionary.viewmodel.DictionaryViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DictionaryViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var getWordsUseCase: GetWordsUseCase
    private lateinit var deleteWordUseCase: DeleteWordUseCase
    private lateinit var viewModel: DictionaryViewModel

    @Before
    fun setup() {
        getWordsUseCase = GetWordsUseCase(mockk(relaxed = true))
        deleteWordUseCase = DeleteWordUseCase(mockk(relaxed = true))
        viewModel = DictionaryViewModel(
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
    fun `LoadWords transitions state to WordsLoaded on success`() = runTest {
        val sampleWords = listOf(
            Word(
                id = "1",
                text = "lernen",
                translations = listOf(
                    Translation(
                        id = "1",
                        text = "learn",
                    ),
                    Translation(
                        id = "2",
                        text = "study",
                    ),
                ),
                examples = emptyList(),
                partOfSpeech = PartOfSpeech.VERB,
            ),
            Word(
                id = "2",
                text = "Haus",
                translations = listOf(
                    Translation(
                        id = "3",
                        text = "house",
                    ),
                ),
                examples = emptyList(),
                partOfSpeech = PartOfSpeech.NOUN,
            ),
        )
        coEvery { getWordsUseCase.execute() } returns sampleWords

        viewModel.state.test {
            viewModel.handleEvent(DictionaryEvent.LoadWords)

            assertEquals(DictionaryState.Empty, awaitItem())
            assertEquals(DictionaryState.Loading, awaitItem())
            assertEquals(
                DictionaryState.WordsLoaded(sampleWords.map { it.toUiModel() }),
                awaitItem(),
            )
            coVerify(exactly = 1) { getWordsUseCase.execute() }
        }
    }

    @Test
    fun `AddWordClicked triggers correct handling`() = runTest {
        viewModel.state.test {
            viewModel.handleEvent(DictionaryEvent.AddWordClicked)

            // No state change expected for this event
            assertEquals(DictionaryState.Empty, awaitItem())
        }
    }

    @Test
    fun `LoadWords handles error gracefully`() = runTest {
        val errorMessage = "Error loading words"
        coEvery { getWordsUseCase.execute() } throws Exception(errorMessage)

        viewModel.state.test {
            viewModel.handleEvent(DictionaryEvent.LoadWords)

            assertEquals(DictionaryState.Empty, awaitItem())
            assertEquals(DictionaryState.Loading, awaitItem())
            assertEquals(DictionaryState.Error(errorMessage), awaitItem())
            coVerify(exactly = 1) { getWordsUseCase.execute() }
        }
    }

    @Test
    fun `LoadWords handles empty list gracefully`() = runTest {
        coEvery { getWordsUseCase.execute() } returns emptyList()

        viewModel.state.test {
            viewModel.handleEvent(DictionaryEvent.LoadWords)

            assertEquals(DictionaryState.Empty, awaitItem())
            assertEquals(DictionaryState.Loading, awaitItem())
            assertEquals(DictionaryState.Empty, awaitItem())
            coVerify(exactly = 1) { getWordsUseCase.execute() }
        }
    }

    @Test
    fun `LoadWords prevents duplicate calls`() = runTest {
        coEvery { getWordsUseCase.execute() } returns emptyList()

        viewModel.state.test {
            viewModel.handleEvent(DictionaryEvent.LoadWords)
            viewModel.handleEvent(DictionaryEvent.LoadWords)

            assertEquals(DictionaryState.Empty, awaitItem())
            assertEquals(DictionaryState.Loading, awaitItem())
            assertEquals(DictionaryState.Empty, awaitItem())
            coVerify(exactly = 1) { getWordsUseCase.execute() }
        }
    }
}
