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
package com.vocabri.ui.screens.dictionarydetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.usecase.word.DeleteWordUseCase
import com.vocabri.domain.usecase.word.ObserveWordsUseCase
import com.vocabri.logger.logger
import com.vocabri.ui.screens.dictionary.model.toTitleResId
import com.vocabri.ui.screens.dictionarydetails.model.toUiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Handles events and manages state for the Dictionary screen.
 *
 * @property observeWordsUseCase Use case for observing words from the repository.
 * @property deleteWordUseCase Use case for deleting words from the repository.
 */
open class DictionaryDetailsViewModel(
    private val partOfSpeech: PartOfSpeech,
    private val observeWordsUseCase: ObserveWordsUseCase,
    private val deleteWordUseCase: DeleteWordUseCase,
    private val ioScope: CoroutineScope,
) : ViewModel() {
    private val log = logger()

    private val _state = MutableStateFlow<DictionaryDetailsState>(
        DictionaryDetailsState.Loading(partOfSpeech.toTitleResId),
    )
    open val state: StateFlow<DictionaryDetailsState> = _state

    init {
        observeWords()
    }

    // Handles UI events triggered by the Dictionary screen.
    fun handleEvent(event: DictionaryDetailsEvent) {
        log.i { "Handling event: $event" }
        when (event) {
            is DictionaryDetailsEvent.DeleteWordClicked -> handleDeleteWord(event.id)
            is DictionaryDetailsEvent.AddWordClicked -> log.i { "AddWordClicked event received" }
            DictionaryDetailsEvent.OnBackClicked -> Unit // Nothing, handled by View
            is DictionaryDetailsEvent.OnWordClicked -> Unit // Nothing, handled by View
        }
    }

    /**
     * Call this method whenever you want to observe words
     * for a certain part of speech.
     * For example, you might call it on init or on user action.
     */
    private fun observeWords() {
        log.i { "Starting to observe $partOfSpeech words" }
        viewModelScope.launch(ioScope.coroutineContext) {
            try {
                observeWordsUseCase.executeByPartOfSpeech(partOfSpeech)
                    .onStart {
                        _state.value = DictionaryDetailsState.Loading(partOfSpeech.toTitleResId)
                    }
                    .catch { throwable ->
                        when (throwable) {
                            is CancellationException -> {
                                log.w(throwable) { "fetchJob was cancelled: $throwable" }
                            }

                            else -> {
                                log.e(throwable) { "Error while loading words: $throwable" }
                                _state.update {
                                    DictionaryDetailsState.Error(
                                        titleId = partOfSpeech.toTitleResId,
                                        message = throwable.message ?: "Failed to load $partOfSpeech words",
                                    )
                                }
                            }
                        }
                    }
                    .collect { words ->
                        val uiWords = words.map { it.toUiModel() }
                        log.d { "Fetched ${uiWords.size} $partOfSpeech words" }

                        _state.update {
                            if (uiWords.isEmpty()) {
                                log.i { "No words found => Empty state" }
                                DictionaryDetailsState.Empty(partOfSpeech.toTitleResId)
                            } else {
                                log.i { "Loaded ${uiWords.size} words => WordsLoaded state" }
                                DictionaryDetailsState.WordsLoaded(
                                    titleId = partOfSpeech.toTitleResId,
                                    words = uiWords,
                                )
                            }
                        }
                    }
            } catch (e: CancellationException) {
                log.w(e) { "observeWords was cancelled: $e" }
            } catch (e: Exception) {
                log.e(e) { "Error while observing words: $e" }
                _state.update {
                    DictionaryDetailsState.Error(
                        titleId = partOfSpeech.toTitleResId,
                        message = e.message ?: "Failed to load $partOfSpeech words",
                    )
                }
            }
        }
    }

    private fun handleDeleteWord(wordId: String) {
        val currentState = _state.value
        if (currentState is DictionaryDetailsState.WordsLoaded) {
            val newList = currentState.words.filterNot { it.id == wordId }
            _state.update { currentState.copy(words = newList) }
        }

        deleteWordInternally(wordId)
    }

    // Deletes a word using the DeleteWordUseCase and reloads the word list.
    private fun deleteWordInternally(wordId: String) {
        log.i { "Starting to delete word with id: $wordId" }
        viewModelScope.launch(ioScope.coroutineContext) {
            try {
                deleteWordUseCase.execute(wordId)
                log.i { "Word with id: $wordId deleted successfully" }
            } catch (e: Exception) {
                log.e(e) { "Failed to delete word with id: $wordId" }
                _state.update {
                    DictionaryDetailsState.Error(
                        titleId = it.titleId,
                        message = e.message ?: "Failed to delete word",
                    )
                }
            }
        }
    }
}
