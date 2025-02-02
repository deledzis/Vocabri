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
package com.vocabri.ui.addword.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabri.R
import com.vocabri.domain.model.word.Example
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.WordGender
import com.vocabri.domain.usecase.word.AddWordUseCase
import com.vocabri.logger.logger
import com.vocabri.utils.IdGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddWordViewModel(
    private val addWordsUseCase: AddWordUseCase,
    private val idGenerator: IdGenerator,
    private val ioScope: CoroutineScope,
) : ViewModel() {
    private val log = logger()

    private val _state = MutableStateFlow<AddWordState>(AddWordState.Editing())
    val state: StateFlow<AddWordState> = _state

    fun handleEvent(event: AddWordEvent) {
        log.i { "Handling event: $event" }
        when (event) {
            is AddWordEvent.UpdateText -> updateText(event.text)
            AddWordEvent.AddTranslation -> addTranslation()
            is AddWordEvent.RemoveTranslation -> removeTranslation(event.translation)
            is AddWordEvent.UpdateCurrentTranslation -> updateCurrentTranslation(event.translation)
            is AddWordEvent.OnTranslationFieldFocusChange -> onTranslationFieldFocusChange(event.focused)
            is AddWordEvent.UpdatePartOfSpeech -> updatePartOfSpeech(event.partOfSpeech)
            AddWordEvent.SaveWord -> saveWord()
        }
    }

    private fun updateText(text: String) = stateAsEditing()?.let { currentState ->
        log.d { "Current text updated to: $text" }
        _state.update {
            currentState.copy(
                text = text,
                isSaveButtonEnabled = isSaveButtonEnabled(text = text),
            )
        }
    }

    private fun addTranslation() = stateAsEditing()?.let { currentState ->
        if (currentState.currentTranslation.isNotBlank()) {
            val newTranslation = currentState.currentTranslation.trim()
            log.d { "Adding new translation: $newTranslation" }
            _state.update {
                currentState.copy(
                    translations = currentState.translations + newTranslation,
                    currentTranslation = "",
                    showAddTranslationButton = false,
                    isSaveButtonEnabled = isSaveButtonEnabled(currentTranslation = newTranslation),
                )
            }
        }
    }

    private fun removeTranslation(translation: String) = stateAsEditing()?.let { currentState ->
        log.d { "Removing translation: $translation" }
        val updatedTranslations =
            currentState.translations.toMutableList().apply { remove(translation) }
        _state.update {
            currentState.copy(
                translations = updatedTranslations,
                isSaveButtonEnabled = isSaveButtonEnabled(translations = updatedTranslations),
            )
        }
    }

    private fun updateCurrentTranslation(translation: String) = stateAsEditing()?.let { currentState ->
        log.d { "Current translation updated to: $translation" }
        _state.update {
            currentState.copy(
                currentTranslation = translation,
                showAddTranslationButton = translation.isNotBlank() && currentState.isTranslationFieldFocused,
                isSaveButtonEnabled = isSaveButtonEnabled(currentTranslation = translation),
            )
        }
    }

    private fun onTranslationFieldFocusChange(focused: Boolean) = stateAsEditing()?.let { currentState ->
        log.d { "Translation field focus changed to: $focused" }
        _state.update {
            currentState.copy(
                isTranslationFieldFocused = focused,
                showAddTranslationButton = focused && currentState.currentTranslation.isNotBlank(),
            )
        }
    }

    private fun updatePartOfSpeech(partOfSpeech: PartOfSpeech) = stateAsEditing()?.let { currentState ->
        log.d { "Part of speech updated to: $partOfSpeech" }
        _state.update {
            currentState.copy(partOfSpeech = partOfSpeech)
        }
    }

    private fun saveWord() = stateAsEditing()?.let { currentState ->
        log.i { "Initiating saveWord process" }

        // Collect final translations (adding the unsaved one if it exists)
        val finalTranslations = if (currentState.currentTranslation.isNotBlank()) {
            log.d { "Saving unsaved translation: ${currentState.currentTranslation.trim()}" }
            currentState.translations + currentState.currentTranslation.trim()
        } else {
            currentState.translations
        }

        // Basic validation
        if (currentState.text.isBlank() || finalTranslations.isEmpty()) {
            log.w { "Cannot save word: Empty text or translations" }
            _state.update { currentState.copy(errorMessageId = R.string.add_word_empty_field) }
            return@let
        }

        val id = idGenerator.generateStringId()
        val text = currentState.text.trim()
        val translations = finalTranslations.map {
            Translation(
                id = idGenerator.generateStringId(),
                translation = it,
            )
        }
        val examples = emptyList<Example>()

        val word = when (currentState.partOfSpeech) {
            PartOfSpeech.NOUN -> {
                // TODO: For now, we use dummy values for gender and pluralForm,
                //  or retrieve them from currentState if your UI collects them.
                Word.Noun(
                    id = id,
                    text = text,
                    translations = translations,
                    examples = examples,
                    gender = WordGender.NEUTER,
                    pluralForm = "???",
                )
            }

            PartOfSpeech.VERB -> {
                Word.Verb(
                    id = id,
                    text = text,
                    translations = translations,
                    examples = examples,
                    conjugation = "regular",
                    tenseForms = "present",
                )
            }

            PartOfSpeech.ADJECTIVE -> {
                Word.Adjective(
                    id = id,
                    text = text,
                    translations = translations,
                    examples = examples,
                    comparative = null,
                    superlative = null,
                )
            }

            PartOfSpeech.ADVERB -> {
                Word.Adverb(
                    id = id,
                    text = text,
                    translations = translations,
                    examples = examples,
                    comparative = null,
                    superlative = null,
                )
            }

            else -> error("Unsupported part of speech: ${currentState.partOfSpeech}")
        }

        log.d { "Word to save: $word" }
        viewModelScope.launch(ioScope.coroutineContext) {
            try {
                addWordsUseCase.execute(word)
                log.i { "Word saved successfully" }
                _state.update { AddWordState.Saved }
            } catch (e: Exception) {
                log.e(e) { "Failed to save the word" }
                _state.update { AddWordState.Error("Failed to save the word") }
            }
        }
    }

    private fun stateAsEditing() = (_state.value as? AddWordState.Editing)

    private fun isSaveButtonEnabled(
        text: String? = null,
        currentTranslation: String? = null,
        translations: List<String>? = null,
    ): Boolean = stateAsEditing()?.let { currentState ->
        val wordNotEmpty = text?.isNotBlank() ?: currentState.text.isNotBlank()
        val translationsNotEmpty =
            translations?.isNotEmpty() ?: currentState.translations.isNotEmpty()
        val currentTranslationNotEmpty =
            currentTranslation?.isNotBlank() ?: currentState.currentTranslation.isNotBlank()
        wordNotEmpty && (translationsNotEmpty || currentTranslationNotEmpty)
    } ?: false
}
