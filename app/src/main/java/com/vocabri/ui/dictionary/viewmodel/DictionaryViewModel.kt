package com.vocabri.ui.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabri.domain.usecase.word.DeleteWordUseCase
import com.vocabri.domain.usecase.word.GetWordsUseCase
import com.vocabri.ui.dictionary.model.WordUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Handles events and manages state for the Dictionary screen.
 *
 * @property getWordsUseCase Use case for loading words from the repository.
 * @property deleteWordUseCase Use case for deleting words from the repository.
 */
open class DictionaryViewModel(
    private val getWordsUseCase: GetWordsUseCase,
    private val deleteWordUseCase: DeleteWordUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<DictionaryState>(DictionaryState.Empty)
    open val state: StateFlow<DictionaryState> = _state

    // Handles UI events triggered by the Dictionary screen.
    fun handleEvent(event: DictionaryEvent) {
        when (event) {
            is DictionaryEvent.LoadWords -> loadWords()
            is DictionaryEvent.DeleteWordClicked -> deleteWord(event.id)
            is DictionaryEvent.AddWordClicked -> Unit
        }
    }

    private fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = DictionaryState.Loading
            try {
                val words = getWordsUseCase.execute()
                val uiWords = words.map {
                    WordUi(
                        id = it.id,
                        text = it.text,
                        translations = it.translations.joinToString(", "),
                        examples = it.examples,
                        partOfSpeech = it.partOfSpeech,
                        notes = it.notes
                    )
                }
                _state.value = if (uiWords.isEmpty()) {
                    DictionaryState.Empty
                } else {
                    DictionaryState.WordsLoaded(uiWords)
                }
            } catch (e: Exception) {
                _state.value = DictionaryState.Error(e.message ?: "Failed to load words")
            }
        }
    }

    // Deletes a word using the DeleteWordUseCase and reloads the word list.
    private fun deleteWord(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteWordUseCase.execute(id)
            loadWords()
        }
    }
}
