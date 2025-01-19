package com.vocabri.ui.addword

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.usecase.word.AddWordUseCase
import com.vocabri.ui.addword.viewmodel.AddWordEvent
import com.vocabri.ui.addword.viewmodel.AddWordState
import com.vocabri.ui.addword.viewmodel.AddWordViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddWordViewModelTest {

    private lateinit var viewModel: AddWordViewModel
    private val addWordUseCase: AddWordUseCase = AddWordUseCase(mockk(relaxed = true))

    @Before
    fun setup() {
        viewModel = AddWordViewModel(addWordUseCase)
    }

    @Test
    fun `updateText updates the text in state`() = runTest {
        // Act
        viewModel.handleEvent(AddWordEvent.UpdateText("lernen"))

        // Assert
        val state = viewModel.state.first()
        assertEquals("lernen", (state as AddWordState.Editing).text)
    }

    @Test
    fun `addTranslation adds a translation to the list`() = runTest {
        // Arrange
        viewModel.handleEvent(AddWordEvent.UpdateCurrentTranslation("learn"))

        // Act
        viewModel.handleEvent(AddWordEvent.AddTranslation)

        // Assert
        val state = viewModel.state.first()
        assertEquals(listOf("learn"), (state as AddWordState.Editing).translations)
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

        // Assert
        val state = viewModel.state.first()
        assertEquals(listOf("learn"), (state as AddWordState.Editing).translations)
    }

    @Test
    fun `saveWord invokes repository and updates state to Saved`() = runTest {
        // Arrange
        coEvery { addWordUseCase.execute(any()) } returns Unit

        // Act
        viewModel.handleEvent(AddWordEvent.UpdateText("lernen"))
        viewModel.handleEvent(AddWordEvent.UpdateCurrentTranslation("learn"))
        viewModel.handleEvent(AddWordEvent.AddTranslation)
        viewModel.handleEvent(AddWordEvent.UpdatePartOfSpeech(PartOfSpeech.VERB))
        viewModel.handleEvent(AddWordEvent.SaveWord)

        // Assert
        coVerify { addWordUseCase.execute(any()) }
        assertEquals(AddWordState.Saved, viewModel.state.first())
    }
}
