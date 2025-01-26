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
package com.vocabri.ui.dictionarydetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.usecase.word.DeleteWordUseCase
import com.vocabri.domain.usecase.word.GetWordsUseCase
import com.vocabri.logger.logger
import com.vocabri.ui.dictionary.model.toTitleResId
import com.vocabri.ui.dictionarydetails.model.toUiModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Handles events and manages state for the Dictionary screen.
 *
 * @property getWordsUseCase Use case for loading words from the repository.
 * @property deleteWordUseCase Use case for deleting words from the repository.
 */
open class DictionaryDetailsViewModel(
    private val getWordsUseCase: GetWordsUseCase,
    private val deleteWordUseCase: DeleteWordUseCase,
    private val ioScope: CoroutineScope,
) : ViewModel() {
    private val log = logger()

    private val _state = MutableStateFlow<DictionaryDetailsState>(
        DictionaryDetailsState.Empty(titleId = R.string.loading),
    )
    open val state: StateFlow<DictionaryDetailsState> = _state

    protected open var currentPartOfSpeech: PartOfSpeech? = null

    private var loadJob: Job? = null

    // Handles UI events triggered by the Dictionary screen.
    fun handleEvent(event: DictionaryDetailsEvent) {
        log.i { "Handling event: $event" }
        when (event) {
            is DictionaryDetailsEvent.LoadWords -> loadWordsGroup(event.wordGroup)
            is DictionaryDetailsEvent.DeleteWordClicked -> deleteWord(event.id)
            is DictionaryDetailsEvent.AddWordClicked -> log.i { "AddWordClicked event received" }
            DictionaryDetailsEvent.OnBackClicked -> Unit
        }
    }

    private fun loadWordsGroup(wordGroup: String) {
        log.i { "loading $wordGroup words requested" }
        val partOfSpeech = try {
            PartOfSpeech.valueOf(wordGroup)
        } catch (e: IllegalArgumentException) {
            log.e(e) { "Invalid word group: $wordGroup" }
            return
        }
        currentPartOfSpeech = partOfSpeech
        refreshData(partOfSpeech)
    }

    private fun refreshData(partOfSpeech: PartOfSpeech) {
        log.i { "Starting to load $partOfSpeech words, is previous job active: ${loadJob?.isActive}" }
        loadJob?.cancel()
        loadJob = viewModelScope.launch(ioScope.coroutineContext) {
            _state.update { DictionaryDetailsState.Loading(partOfSpeech.toTitleResId) }
            try {
                val words = getWordsUseCase.executeByPartOfSpeech(partOfSpeech)
                log.d { "Fetched ${words.size} $partOfSpeech words" }
                val uiWords = words.map { it.toUiModel() }
                _state.update {
                    if (uiWords.isEmpty()) {
                        log.i { "No words found, setting state to Empty" }
                        DictionaryDetailsState.Empty(partOfSpeech.toTitleResId)
                    } else {
                        log.i { "Loaded ${uiWords.size} words, setting state to WordsLoaded" }
                        DictionaryDetailsState.WordsLoaded(titleId = partOfSpeech.toTitleResId, words = uiWords)
                    }
                }
            } catch (e: CancellationException) {
                log.w(e) { "loadWords was cancelled $e" }
            } catch (e: Exception) {
                log.e(e) { "Error while loading words $e" }
                _state.update {
                    DictionaryDetailsState.Error(
                        titleId = partOfSpeech.toTitleResId,
                        message = e.message ?: "Failed to load $partOfSpeech words",
                    )
                }
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
                currentPartOfSpeech?.let {
                    refreshData(it)
                } ?: run {
                    log.e { "Current group is null, unable to reload data" }
                    _state.update {
                        DictionaryDetailsState.Error(
                            titleId = it.titleId,
                            message = "Failed to reload words: group is not defined",
                        )
                    }
                }
            } catch (e: Exception) {
                log.e(e) { "Failed to delete word with id: $id" }
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
