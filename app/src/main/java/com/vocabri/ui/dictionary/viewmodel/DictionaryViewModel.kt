package com.vocabri.ui.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabri.domain.usecase.word.DeleteWordUseCase
import com.vocabri.domain.usecase.word.GetWordsUseCase
import com.vocabri.domain.util.logger
import com.vocabri.ui.dictionary.model.toUiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private val ioScope: CoroutineScope,
) : ViewModel() {
    private val log = logger()

    private val _state = MutableStateFlow<DictionaryState>(DictionaryState.Empty)
    open val state: StateFlow<DictionaryState> = _state

    private var loadJob: Job? = null

    // Handles UI events triggered by the Dictionary screen.
    fun handleEvent(event: DictionaryEvent) {
        log.i { "Handling event: $event" }
        when (event) {
            is DictionaryEvent.LoadWords -> loadWords()
            is DictionaryEvent.DeleteWordClicked -> deleteWord(event.id)
            is DictionaryEvent.AddWordClicked -> log.i { "AddWordClicked event received" }
        }
    }

    private fun loadWords() {
        log.i { "Starting to load words, is previous job active: ${loadJob?.isActive}" }
        loadJob?.cancel()
        loadJob = viewModelScope.launch(ioScope.coroutineContext) {
            _state.value = DictionaryState.Loading
            delay(2000)
            try {
                val words = getWordsUseCase.execute()
                log.d { "Fetched ${words.size} words from use case" }
                val uiWords = words.map { it.toUiModel() }
                _state.value = if (uiWords.isEmpty()) {
                    log.i { "No words found, setting state to Empty" }
                    DictionaryState.Empty
                } else {
                    log.i { "Loaded ${uiWords.size} words, setting state to WordsLoaded" }
                    DictionaryState.WordsLoaded(uiWords)
                }
            } catch (e: CancellationException) {
                log.w(e) { "loadWords was cancelled" }
            } catch (e: Exception) {
                log.e(e) { "Error while loading words" }
                _state.value = DictionaryState.Error(e.message ?: "Failed to load words")
            }
        }
    }

    // Deletes a word using the DeleteWordUseCase and reloads the word list.
    private fun deleteWord(id: String) {
        log.i { "Starting to delete word with id: $id" }
        viewModelScope.launch(ioScope.coroutineContext) {
            try {
                deleteWordUseCase.execute(id)
                log.i { "Word with id: $id deleted successfully" }
                loadWords()
            } catch (e: Exception) {
                log.e(e) { "Failed to delete word with id: $id" }
                _state.value = DictionaryState.Error(e.message ?: "Failed to delete word")
            }
        }
    }
}
