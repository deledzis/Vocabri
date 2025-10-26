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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

open class DictionaryViewModel(
    private val observeWordGroupsUseCase: ObserveWordGroupsUseCase,
    private val resourcesRepository: ResourcesRepository,
    private val ioScope: CoroutineScope,
) : ViewModel() {

    private val log = logger()

    private val _state = MutableStateFlow<DictionaryState>(DictionaryState.Loading)
    open val state: StateFlow<DictionaryState> = _state

    private val _effect = MutableSharedFlow<DictionaryEffect>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    open val effect: SharedFlow<DictionaryEffect> = _effect.asSharedFlow()

    private var observeJob: Job? = null

    init {
        observeWordGroups()
    }

    fun handleEvent(event: DictionaryEvent) {
        log.i { "Handling event: $event" }
        when (event) {
            DictionaryEvent.AddWordClicked -> sendEffect(DictionaryEffect.NavigateToAddWord)
            is DictionaryEvent.OnGroupCardClicked ->
                sendEffect(DictionaryEffect.NavigateToDictionaryDetails(event.partOfSpeech))

            DictionaryEvent.RetryClicked -> retry()
        }
    }

    fun retry() {
        log.i { "Retry requested from UI" }
        observeWordGroups()
    }

    private fun observeWordGroups() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch(ioScope.coroutineContext) {
            observeWordGroupsUseCase.execute()
                .onStart {
                    _state.value = DictionaryState.Loading
                }
                .catch { throwable ->
                    when (throwable) {
                        is CancellationException -> {
                            log.w(throwable) { "observeWordGroups was cancelled: $throwable" }
                        }

                        else -> {
                            log.e(throwable) { "observeWordGroups failed due to exception: $throwable" }
                            _state.value = DictionaryState.Error("Failed to load data, please try again later.")
                        }
                    }
                }
                .collect { wordGroups ->
                    val allWordsGroupUiModel = wordGroups.allWords.toUiModel()
                    val groupsUiModel = wordGroups.groups.map { it.toUiModel() }

                    _state.value = if (groupsUiModel.isEmpty()) {
                        DictionaryState.Empty
                    } else {
                        DictionaryState.GroupsLoaded(
                            allWords = allWordsGroupUiModel,
                            groups = groupsUiModel,
                        )
                    }
                }
        }
    }

    private fun sendEffect(effect: DictionaryEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun WordGroup.toUiModel(): WordGroupUiModel = WordGroupUiModel(
        partOfSpeech = partOfSpeech,
        titleText = resourcesRepository.getString(partOfSpeech.toTitleResId),
        subtitleText = resourcesRepository.getString(R.string.dictionary_group_words_count, wordCount),
    )
}
