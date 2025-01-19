package com.vocabri.ui.dictionary

import app.cash.turbine.test
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.usecase.word.DeleteWordUseCase
import com.vocabri.domain.usecase.word.GetWordsUseCase
import com.vocabri.ui.dictionary.model.WordUi
import com.vocabri.ui.dictionary.viewmodel.DictionaryEvent
import com.vocabri.ui.dictionary.viewmodel.DictionaryState
import com.vocabri.ui.dictionary.viewmodel.DictionaryViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DictionaryViewModelTest {

    private lateinit var viewModel: DictionaryViewModel
    private val getWordsUseCase: GetWordsUseCase = GetWordsUseCase(mockk(relaxed = true))
    private val deleteWordUseCase: DeleteWordUseCase = DeleteWordUseCase(mockk(relaxed = true))

    @Before
    fun setup() {
        viewModel = DictionaryViewModel(getWordsUseCase, deleteWordUseCase)
    }

    @Test
    fun `LoadWords loads words successfully`() = runTest {
        val sampleWords = listOf(
            Word("1", "lernen", listOf("learn", "study"), emptyList(), PartOfSpeech.VERB),
            Word("2", "Haus", listOf("house"), emptyList(), PartOfSpeech.NOUN)
        )
        coEvery { getWordsUseCase.execute() } returns sampleWords
        val sampleWordsUi = listOf(
            WordUi("1", "lernen", "learn, study", emptyList(), PartOfSpeech.VERB),
            WordUi("2", "Haus", "house", emptyList(), PartOfSpeech.NOUN)
        )

        viewModel.state.test {
            viewModel.handleEvent(DictionaryEvent.LoadWords)

            assertEquals(DictionaryState.Empty, awaitItem())
            assertEquals(DictionaryState.Loading, awaitItem())
            assertEquals(DictionaryState.WordsLoaded(sampleWordsUi), awaitItem())
        }
    }

    @Test
    fun `AddWordClicked triggers correct handling`() = runTest {
        viewModel.state.test {
            viewModel.handleEvent(DictionaryEvent.AddWordClicked)

            // Placeholder for additional assertions (if needed)
            assertEquals(DictionaryState.Empty, awaitItem()) // State doesn't change for this intent
        }
    }
}
