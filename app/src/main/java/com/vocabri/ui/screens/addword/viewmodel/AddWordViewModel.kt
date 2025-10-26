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
package com.vocabri.ui.screens.addword.viewmodel

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddWordViewModel(private val addWordsUseCase: AddWordUseCase, private val idGenerator: IdGenerator) :
    ViewModel() {
    private val log = logger()

    private val _state = MutableStateFlow<AddWordState>(AddWordState.Editing())
    val state: StateFlow<AddWordState> = _state

    private val _effect = MutableSharedFlow<AddWordEffect>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effect: SharedFlow<AddWordEffect> = _effect.asSharedFlow()

    fun handleEvent(event: AddWordEvent) {
        log.i { "Handling event: $event" }
        when (event) {
            is AddWordEvent.UpdateText -> updateText(event.text)
            is AddWordEvent.UpdatePartOfSpeech -> updatePartOfSpeech(event.partOfSpeech)

            // Translations
            is AddWordEvent.UpdateCurrentTranslation -> updateCurrentTranslation(event.translation)
            AddWordEvent.AddTranslation -> addTranslation()
            is AddWordEvent.RemoveTranslation -> removeTranslation(event.translation)
            is AddWordEvent.OnTranslationFieldFocusChange -> onTranslationFieldFocusChange(event.focused)

            // Examples
            is AddWordEvent.UpdateCurrentExample -> updateCurrentExample(event.example)
            AddWordEvent.AddExample -> addExample()
            is AddWordEvent.RemoveExample -> removeExample(event.example)
            is AddWordEvent.OnExampleFieldFocusChange -> onExampleFieldFocusChange(event.focused)

            // Noun
            is AddWordEvent.UpdateNounGender -> updateNounGender(event.gender)
            is AddWordEvent.UpdatePluralForm -> updateNounPlural(event.plural)

            // Verb
            is AddWordEvent.UpdateCurrentConjugation -> updateCurrentConjugation(event.conjugation)
            AddWordEvent.AddConjugation -> addConjugation()
            is AddWordEvent.RemoveConjugation -> removeConjugation(event.conjugation)
            is AddWordEvent.OnConjugationFieldFocusChange -> onConjugationFieldFocusChange(event.focused)

            is AddWordEvent.UpdateCurrentManagement -> updateCurrentManagement(event.management)
            AddWordEvent.AddManagement -> addManagement()
            is AddWordEvent.RemoveManagement -> removeManagement(event.management)
            is AddWordEvent.OnManagementFieldFocusChange -> onManagementFieldFocusChange(event.focused)

            // Adjective/Adverb
            is AddWordEvent.UpdateComparative -> updateComparative(event.comparative)
            is AddWordEvent.UpdateSuperlative -> updateSuperlative(event.superlative)
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

    private fun updatePartOfSpeech(partOfSpeech: PartOfSpeech) = stateAsEditing()?.let { currentState ->
        log.d { "Part of speech updated to: $partOfSpeech" }
        _state.update {
            currentState.copy(partOfSpeech = partOfSpeech)
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

    private fun onTranslationFieldFocusChange(focused: Boolean) = stateAsEditing()?.let { currentState ->
        log.d { "Translation field focus changed to: $focused" }
        _state.update {
            currentState.copy(
                isTranslationFieldFocused = focused,
                showAddTranslationButton = focused && currentState.currentTranslation.isNotBlank(),
            )
        }
    }

    // region: Examples
    private fun updateCurrentExample(example: String) = stateAsEditing()?.let { currentState ->
        log.d { "Current example updated to: $example" }
        _state.update {
            currentState.copy(
                currentExample = example,
                showAddExampleButton = example.isNotBlank() && currentState.isExampleFieldFocused,
            )
        }
    }

    private fun addExample() = stateAsEditing()?.let { currentState ->
        if (currentState.currentExample.isNotBlank()) {
            val newExample = currentState.currentExample.trim()
            log.d { "Adding new example: $newExample" }
            val newList = currentState.examples + newExample
            _state.update {
                currentState.copy(
                    examples = newList,
                    currentExample = "",
                    showAddExampleButton = false,
                )
            }
        }
    }

    private fun removeExample(example: String) = stateAsEditing()?.let { currentState ->
        log.d { "Removing example: $example" }
        val updated = currentState.examples.toMutableList().apply { remove(example) }
        _state.update {
            currentState.copy(
                examples = updated,
            )
        }
    }

    private fun onExampleFieldFocusChange(focused: Boolean) = stateAsEditing()?.let { currentState ->
        log.d { "Example field focus changed to: $focused" }
        _state.update {
            currentState.copy(
                isExampleFieldFocused = focused,
                showAddExampleButton = focused && currentState.currentExample.isNotBlank(),
            )
        }
    }
    // endregion

    // region: Noun
    private fun updateNounGender(gender: WordGender) = stateAsEditing()?.let { currentState ->
        log.d { "Noun gender updated to: $gender" }
        _state.update {
            // allow toggle selection of gender
            currentState.copy(selectedGender = if (currentState.selectedGender == gender) null else gender)
        }
    }

    private fun updateNounPlural(plural: String) = stateAsEditing()?.let { currentState ->
        log.d { "Noun plural form updated to: $plural" }
        _state.update {
            currentState.copy(pluralForm = plural)
        }
    }
    // endregion

    // region: Verb
    private fun updateCurrentConjugation(conjugation: String) = stateAsEditing()?.let { currentState ->
        log.d { "Current conjugation updated: $conjugation" }
        _state.update {
            currentState.copy(
                currentConjugation = conjugation,
                showAddConjugationButton = conjugation.isNotBlank() && currentState.isConjugationFieldFocused,
            )
        }
    }

    private fun addConjugation() = stateAsEditing()?.let { currentState ->
        if (currentState.currentConjugation.isNotBlank()) {
            val newConjugation = currentState.currentConjugation.trim()
            log.d { "Adding new conjugation: $newConjugation" }
            val newList = currentState.conjugations + newConjugation
            _state.update {
                currentState.copy(
                    conjugations = newList,
                    currentConjugation = "",
                    showAddConjugationButton = false,
                )
            }
        }
    }

    private fun removeConjugation(conjugation: String) = stateAsEditing()?.let { currentState ->
        log.d { "Removing conjugation: $conjugation" }
        val updated = currentState.conjugations.toMutableList().apply { remove(conjugation) }
        _state.update {
            currentState.copy(conjugations = updated)
        }
    }

    private fun onConjugationFieldFocusChange(focused: Boolean) = stateAsEditing()?.let { currentState ->
        log.d { "Conjugation field focus changed to: $focused" }
        _state.update {
            currentState.copy(
                isConjugationFieldFocused = focused,
                showAddConjugationButton = focused && currentState.currentConjugation.isNotBlank(),
            )
        }
    }

    private fun updateCurrentManagement(management: String) = stateAsEditing()?.let { currentState ->
        log.d { "Current management updated: $management" }
        _state.update {
            currentState.copy(
                currentManagement = management,
                showAddManagementButton = management.isNotBlank() && currentState.isManagementFieldFocused,
            )
        }
    }

    private fun addManagement() = stateAsEditing()?.let { currentState ->
        if (currentState.currentManagement.isNotBlank()) {
            val newManagement = currentState.currentManagement.trim()
            log.d { "Adding new management: $newManagement" }
            val newList = currentState.managements + newManagement
            _state.update {
                currentState.copy(
                    managements = newList,
                    currentManagement = "",
                    showAddManagementButton = false,
                )
            }
        }
    }

    private fun removeManagement(management: String) = stateAsEditing()?.let { currentState ->
        log.d { "Removing management: $management" }
        val updated = currentState.managements.toMutableList().apply { remove(management) }
        _state.update {
            currentState.copy(managements = updated)
        }
    }

    private fun onManagementFieldFocusChange(focused: Boolean) = stateAsEditing()?.let { currentState ->
        log.d { "Management field focus changed to: $focused" }
        _state.update {
            currentState.copy(
                isManagementFieldFocused = focused,
                showAddManagementButton = focused && currentState.currentManagement.isNotBlank(),
            )
        }
    }
    // endregion

    // region: Adjective / Adverb
    private fun updateComparative(comparative: String) = stateAsEditing()?.let { currentState ->
        log.d { "Comparative updated: $comparative" }
        _state.update {
            currentState.copy(comparative = comparative)
        }
    }

    private fun updateSuperlative(superlative: String) = stateAsEditing()?.let { currentState ->
        log.d { "Superlative updated: $superlative" }
        _state.update {
            currentState.copy(superlative = superlative)
        }
    }
    // endregion

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
            log.e { "Cannot save word: Empty text or translations" }
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

        val finalExamples = currentState.examples.map {
            Example(
                id = idGenerator.generateStringId(),
                example = it,
            )
        }

        val word = when (currentState.partOfSpeech) {
            PartOfSpeech.NOUN -> Word.Noun(
                id = id,
                text = text,
                translations = translations,
                examples = finalExamples,
                gender = currentState.selectedGender,
                pluralForm = currentState.pluralForm,
            )

            PartOfSpeech.VERB -> Word.Verb(
                id = id,
                text = text,
                translations = translations,
                examples = finalExamples,
                // TODO: how to save and read back from DB properly?
                conjugation = currentState.conjugations.joinToString(", "),
                management = currentState.managements.joinToString(", "),
            )

            PartOfSpeech.ADJECTIVE -> Word.Adjective(
                id = id,
                text = text,
                translations = translations,
                examples = finalExamples,
                comparative = currentState.comparative,
                superlative = currentState.superlative,
            )

            PartOfSpeech.ADVERB -> Word.Adverb(
                id = id,
                text = text,
                translations = translations,
                examples = finalExamples,
                comparative = currentState.comparative,
                superlative = currentState.superlative,
            )

            else -> error("Unsupported part of speech")
        }

        log.d { "Word to save: $word" }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                addWordsUseCase.execute(word)
                log.i { "Word saved successfully" }
                onWordSaved()
            } catch (e: IllegalStateException) {
                log.e(e) { "Word already exists, ignore and leave: $e" }
                // TODO: word already exists, we should update existing word merging with the data user provided here
                onWordSaved()
            } catch (e: Exception) {
                log.e(e) { "Failed to save the word: $e" }
                sendEffect(AddWordEffect.ShowError("Failed to save the word"))
            }
        }
    }

    private fun onWordSaved() {
        _state.update { AddWordState.Editing() }
        sendEffect(AddWordEffect.WordSaved)
    }

    private fun sendEffect(effect: AddWordEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
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
