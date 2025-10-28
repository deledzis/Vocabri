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
package com.vocabri.ui.screens.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabri.R
import com.vocabri.domain.model.word.WordGroup
import com.vocabri.domain.repository.ResourcesRepository
import com.vocabri.domain.usecase.word.ObserveWordGroupsUseCase
import com.vocabri.logger.logger
import com.vocabri.ui.screens.dictionary.model.WordGroupUiModel
import com.vocabri.ui.screens.dictionary.model.toTitleResId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

open class DictionaryViewModel(
    private val observeWordGroupsUseCase: ObserveWordGroupsUseCase,
    private val resourcesRepository: ResourcesRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val log = logger()

    private val _uiState = MutableStateFlow<DictionaryContract.UiState>(DictionaryContract.UiState.Loading)
    open val uiState: StateFlow<DictionaryContract.UiState> = _uiState

    private var observeJob: Job? = null

    init {
        observeWordGroups()
    }

    fun onEvent(event: DictionaryContract.UiEvent) {
        log.i { "Handling event: $event" }
        when (event) {
            DictionaryContract.UiEvent.Retry -> reloadWordGroups()
        }
    }

    private fun reloadWordGroups() {
        observeWordGroups()
    }

    private fun observeWordGroups() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch(ioDispatcher) {
            observeWordGroupsUseCase.execute()
                .onStart {
                    _uiState.value = DictionaryContract.UiState.Loading
                }
                .catch { throwable ->
                    when (throwable) {
                        is CancellationException -> {
                            log.w(throwable) { "observeWordGroups was cancelled: $throwable" }
                        }

                        else -> {
                            log.e(throwable) { "observeWordGroups failed due to exception: $throwable" }
                            _uiState.value =
                                DictionaryContract.UiState.Error("Failed to load data, please try again later.")
                        }
                    }
                }
                .collect { wordGroups ->
                    val allWordsGroupUiModel = wordGroups.allWords.toUiModel()
                    val groupsUiModel = wordGroups.groups.map { it.toUiModel() }

                    _uiState.value = if (groupsUiModel.isEmpty()) {
                        DictionaryContract.UiState.Empty
                    } else {
                        DictionaryContract.UiState.GroupsLoaded(
                            allWords = allWordsGroupUiModel,
                            groups = groupsUiModel,
                        )
                    }
                }
        }
    }

    private fun WordGroup.toUiModel(): WordGroupUiModel = WordGroupUiModel(
        partOfSpeech = partOfSpeech,
        titleText = resourcesRepository.getString(partOfSpeech.toTitleResId),
        subtitleText = resourcesRepository.getString(R.string.dictionary_group_words_count, wordCount),
    )
}
