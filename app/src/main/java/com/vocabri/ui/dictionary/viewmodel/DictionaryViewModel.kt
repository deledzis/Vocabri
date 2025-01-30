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
package com.vocabri.ui.dictionary.viewmodel

import androidx.lifecycle.ViewModel
import com.vocabri.R
import com.vocabri.domain.model.word.WordGroup
import com.vocabri.domain.repository.ResourcesRepository
import com.vocabri.domain.usecase.word.GetWordGroupsUseCase
import com.vocabri.logger.logger
import com.vocabri.ui.dictionary.model.WordGroupUiModel
import com.vocabri.ui.dictionary.model.toTitleResId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

open class DictionaryViewModel(
    private val getWordGroupsUseCase: GetWordGroupsUseCase,
    private val resourcesRepository: ResourcesRepository,
    private val ioScope: CoroutineScope,
) : ViewModel() {

    private val log = logger()

    private val _state = MutableStateFlow<DictionaryState>(DictionaryState.Empty)
    open val state: StateFlow<DictionaryState> = _state

    private var loadJob: Job? = null

    // Handles UI events triggered by the Dictionary screen.
    fun handleEvent(event: DictionaryEvent) {
        when (event) {
            is DictionaryEvent.LoadWords -> loadWordGroups()
            is DictionaryEvent.OnGroupCardClicked -> Unit // No action needed
            is DictionaryEvent.AddWordClicked -> Unit // No action needed
        }
    }

    // Loads groups of words by part of speech.
    private fun loadWordGroups() {
        loadJob?.cancel()
        loadJob = ioScope.launch {
            _state.value = DictionaryState.Loading
            try {
                val wordGroups = getWordGroupsUseCase.execute()
                val allWordsGroupUiModel = wordGroups.allWords.toUiModel()
                val groupsUiModel = wordGroups.groups.map { it.toUiModel() }
                _state.value = if (groupsUiModel.isEmpty()) {
                    DictionaryState.Empty
                } else {
                    DictionaryState.GroupsLoaded(allWords = allWordsGroupUiModel, groups = groupsUiModel)
                }
            } catch (e: CancellationException) {
                log.w(e) { "loadWordGroups was cancelled" }
            } catch (e: Exception) {
                _state.value = DictionaryState.Error("Failed to load word groups")
            }
        }
    }

    private fun WordGroup.toUiModel(): WordGroupUiModel = WordGroupUiModel(
        partOfSpeech = partOfSpeech,
        titleText = resourcesRepository.getString(partOfSpeech.toTitleResId),
        subtitleText = resourcesRepository.getString(R.string.dictionary_group_words_count, wordCount),
    )
}
