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
package com.vocabri.ui.addword

import com.vocabri.data.test.FakeIdGenerator
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository
import com.vocabri.domain.usecase.word.AddWordUseCase
import com.vocabri.rules.MainDispatcherRule
import com.vocabri.ui.screens.addword.viewmodel.AddWordEvent
import com.vocabri.ui.screens.addword.viewmodel.AddWordState
import com.vocabri.ui.screens.addword.viewmodel.AddWordViewModel
import com.vocabri.utils.IdGenerator
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
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
class AddWordViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var idGenerator: IdGenerator
    private lateinit var mockWordRepository: WordRepository
    private lateinit var addWordUseCase: AddWordUseCase
    private lateinit var viewModel: AddWordViewModel

    @Before
    fun setup() {
        idGenerator = FakeIdGenerator()
        mockWordRepository = mockk(relaxed = true)
        addWordUseCase = AddWordUseCase(mockWordRepository)
        viewModel = AddWordViewModel(
            addWordsUseCase = addWordUseCase,
            idGenerator = idGenerator,
            ioScope = TestScope(dispatcherRule.testDispatcher),
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `updateText updates the text in state`() = runTest {
        // Act
        viewModel.handleEvent(AddWordEvent.UpdateText("lernen"))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue(state is AddWordState.Editing)
        assertEquals("lernen", (state as AddWordState.Editing).text)
    }

    @Test
    fun `addTranslation adds a translation and clears current translation`() = runTest {
        // Arrange
        viewModel.handleEvent(AddWordEvent.UpdateCurrentTranslation("learn"))

        // Act
        viewModel.handleEvent(AddWordEvent.AddTranslation)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue(state is AddWordState.Editing)
        state as AddWordState.Editing
        assertEquals(listOf("learn"), state.translations)
        assertEquals("", state.currentTranslation)
    }

    @Test
    fun `removeTranslation removes the specified translation`() = runTest {
        // Arrange
        viewModel.handleEvent(AddWordEvent.UpdateCurrentTranslation("learn"))
        viewModel.handleEvent(AddWordEvent.AddTranslation)
        viewModel.handleEvent(AddWordEvent.UpdateCurrentTranslation("study"))
        viewModel.handleEvent(AddWordEvent.AddTranslation)

        // Act
        viewModel.handleEvent(AddWordEvent.RemoveTranslation("study"))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue(state is AddWordState.Editing)
        state as AddWordState.Editing
        assertEquals(listOf("learn"), state.translations)
    }

    @Test
    fun `removeTranslation does nothing if translation does not exist`() = runTest {
        // Arrange
        viewModel.handleEvent(AddWordEvent.UpdateCurrentTranslation("learn"))
        viewModel.handleEvent(AddWordEvent.AddTranslation)

        // Act
        viewModel.handleEvent(AddWordEvent.RemoveTranslation("study"))
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue(state is AddWordState.Editing)
        state as AddWordState.Editing
        assertEquals(listOf("learn"), state.translations)
    }

    @Test
    fun `saveWord invokes repository with correct data when partOfSpeech is VERB`() = runTest {
        // Arrange
        coEvery { addWordUseCase.execute(any()) } returns Unit

        // Act
        viewModel.handleEvent(AddWordEvent.UpdateText("lernen"))
        viewModel.handleEvent(AddWordEvent.UpdateCurrentTranslation("learn"))
        viewModel.handleEvent(AddWordEvent.AddTranslation)
        viewModel.handleEvent(AddWordEvent.UpdatePartOfSpeech(PartOfSpeech.VERB))
        viewModel.handleEvent(AddWordEvent.UpdateCurrentConjugation("regular"))
        viewModel.handleEvent(AddWordEvent.AddConjugation)
        viewModel.handleEvent(AddWordEvent.UpdateCurrentManagement("auf + Akk."))
        viewModel.handleEvent(AddWordEvent.AddManagement)

        // Act
        viewModel.handleEvent(AddWordEvent.SaveWord)
        advanceUntilIdle()

        // Assert
        val expectedWord = Word.Verb(
            id = idGenerator.generateStringId(),
            text = "lernen",
            translations = listOf(
                Translation(
                    id = idGenerator.generateStringId(),
                    translation = "learn",
                ),
            ),
            examples = emptyList(),
            conjugation = "regular",
            management = "auf + Akk.",
        )

        coVerify(exactly = 1) {
            addWordUseCase.execute(expectedWord)
        }
        assertEquals(AddWordState.Saved, viewModel.state.first())
    }

    @Test
    fun `saveWord sets error state on failure`() = runTest {
        // Arrange
        val errorMessage = "Failed to save the word"
        coEvery { addWordUseCase.execute(any()) } throws Exception(errorMessage)

        // Act
        viewModel.handleEvent(AddWordEvent.UpdateText("lernen"))
        viewModel.handleEvent(AddWordEvent.UpdateCurrentTranslation("learn"))
        viewModel.handleEvent(AddWordEvent.AddTranslation)
        viewModel.handleEvent(AddWordEvent.UpdatePartOfSpeech(PartOfSpeech.VERB))
        viewModel.handleEvent(AddWordEvent.SaveWord)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue(state is AddWordState.Error)
        state as AddWordState.Error
        assertEquals("Failed to save the word", state.message)
    }

    @Test
    fun `saveWord sets saved state when repository throws word is already saved error`() = runTest {
        // Arrange
        val errorMessage = "Failed to save the word"
        coEvery { addWordUseCase.execute(any()) } throws IllegalStateException(errorMessage)

        // Act
        viewModel.handleEvent(AddWordEvent.UpdateText("lernen"))
        viewModel.handleEvent(AddWordEvent.UpdateCurrentTranslation("learn"))
        viewModel.handleEvent(AddWordEvent.AddTranslation)
        viewModel.handleEvent(AddWordEvent.UpdatePartOfSpeech(PartOfSpeech.VERB))
        viewModel.handleEvent(AddWordEvent.SaveWord)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            addWordUseCase.execute(any())
        }
        assertEquals(AddWordState.Saved, viewModel.state.first())
    }
}
