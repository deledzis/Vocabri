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

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.ResourcesRepository
import com.vocabri.domain.repository.WordRepository
import com.vocabri.domain.usecase.word.ObserveWordGroupsUseCase
import com.vocabri.rules.MainDispatcherRule
import com.vocabri.ui.screens.dictionary.viewmodel.DictionaryState
import com.vocabri.ui.screens.dictionary.viewmodel.DictionaryViewModel
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
class DictionaryViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var wordRepository: WordRepository
    private lateinit var resourcesRepository: ResourcesRepository

    private fun createViewModel(flow: Flow<List<Word>>): DictionaryViewModel {
        wordRepository = mockk(relaxed = true)
        resourcesRepository = mockk(relaxed = true)
        every { wordRepository.observeWordsByPartOfSpeech(PartOfSpeech.ALL) } returns flow

        val useCase = ObserveWordGroupsUseCase(wordRepository)
        return DictionaryViewModel(
            observeWordGroupsUseCase = useCase,
            resourcesRepository = resourcesRepository,
            ioScope = TestScope(dispatcherRule.testDispatcher),
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `emits Empty when no items available`() = runTest {
        val viewModel = createViewModel(flowOf(emptyList()))
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(DictionaryState.Empty, state)
    }

    @Test
    fun `emits Error when repository fails`() = runTest {
        val viewModel = createViewModel(
            flow {
                throw IllegalStateException("boom")
            },
        )
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertTrue(state is DictionaryState.Error)
        assertEquals(
            "Failed to load data, please try again later.",
            (state as DictionaryState.Error).message,
        )
    }

    @Test
    fun `Retry re-subscribes and recovers to Empty`() = runTest {
        val flows = mutableListOf<Flow<List<Word>>>()
        val failingFlow = flow<List<Word>> { throw IllegalStateException("boom") }
        val emptyFlow = flowOf(emptyList<Word>())
        flows.add(failingFlow)
        flows.add(emptyFlow)

        wordRepository = mockk(relaxed = true)
        resourcesRepository = mockk(relaxed = true)
        every { wordRepository.observeWordsByPartOfSpeech(PartOfSpeech.ALL) } answers {
            flows.removeAt(0)
        }

        val viewModel = DictionaryViewModel(
            observeWordGroupsUseCase = ObserveWordGroupsUseCase(wordRepository),
            resourcesRepository = resourcesRepository,
            ioScope = TestScope(dispatcherRule.testDispatcher),
        )

        advanceUntilIdle()
        // Initially error
        assertTrue(viewModel.state.first() is DictionaryState.Error)

        // Retry should switch to Empty
        viewModel.retry()
        advanceUntilIdle()
        assertEquals(DictionaryState.Empty, viewModel.state.first())
    }
}
