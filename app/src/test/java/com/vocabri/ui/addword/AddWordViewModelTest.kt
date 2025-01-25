package com.vocabri.ui.addword

import app.cash.turbine.test
import com.vocabri.data.test.FakeIdGenerator
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository
import com.vocabri.domain.usecase.word.AddWordUseCase
import com.vocabri.domain.util.IdGenerator
import com.vocabri.rules.MainDispatcherRule
import com.vocabri.ui.addword.viewmodel.AddWordEvent
import com.vocabri.ui.addword.viewmodel.AddWordState
import com.vocabri.ui.addword.viewmodel.AddWordViewModel
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
        mockWordRepository = mockk<WordRepository>(relaxed = true)
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
        viewModel.state.test {
            // Act
            viewModel.handleEvent(AddWordEvent.UpdateText("lernen"))

            // Assert
            val state = awaitItem()
            assertTrue(state is AddWordState.Editing)
            println("STATE $state")
            assertEquals("lernen", (state as AddWordState.Editing).text)
        }
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
        assertEquals(listOf("learn"), (state as AddWordState.Editing).translations)
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
        assertEquals(listOf("learn"), (state as AddWordState.Editing).translations)
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
        assertEquals(listOf("learn"), (state as AddWordState.Editing).translations)
    }

    @Test
    fun `saveWord invokes repository with correct data`() = runTest {
        // Arrange
        coEvery { addWordUseCase.execute(any()) } returns Unit

        // Act
        viewModel.handleEvent(AddWordEvent.UpdateText("lernen"))
        viewModel.handleEvent(AddWordEvent.UpdateCurrentTranslation("learn"))
        viewModel.handleEvent(AddWordEvent.AddTranslation)
        viewModel.handleEvent(AddWordEvent.UpdatePartOfSpeech(PartOfSpeech.VERB))

        // Act
        viewModel.handleEvent(AddWordEvent.SaveWord)
        advanceUntilIdle()

        // Assert
        val expectedWord = Word(
            id = idGenerator.generateStringId(),
            text = "lernen",
            translations = listOf(Translation(idGenerator.generateStringId(), "learn")),
            examples = emptyList(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = null,
        )
        coVerify { addWordUseCase.execute(expectedWord) }
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

        // Act
        viewModel.handleEvent(AddWordEvent.SaveWord)
        advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue(state is AddWordState.Error)
        assertEquals("Failed to save the word", (state as AddWordState.Error).message)
    }
}
