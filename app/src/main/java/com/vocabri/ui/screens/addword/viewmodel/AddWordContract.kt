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

import androidx.annotation.StringRes
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.WordGender

object AddWordContract {
    sealed interface UiState {
        data class Editing(
            val text: String = "",
            // Translations
            val translations: List<String> = emptyList(),
            val currentTranslation: String = "",
            val isTranslationFieldFocused: Boolean = false,
            val showAddTranslationButton: Boolean = false,

            // Examples
            val examples: List<String> = emptyList(),
            val currentExample: String = "",
            val isExampleFieldFocused: Boolean = false,
            val showAddExampleButton: Boolean = false,

            // Part of Speech
            val partOfSpeech: PartOfSpeech = PartOfSpeech.NOUN,

            // Noun-specific
            val selectedGender: WordGender? = null,
            val pluralForm: String = "",

            // Verb-specific
            val conjugations: List<String> = emptyList(),
            val currentConjugation: String = "",
            val isConjugationFieldFocused: Boolean = false,
            val showAddConjugationButton: Boolean = false,

            val managements: List<String> = emptyList(),
            val currentManagement: String = "",
            val isManagementFieldFocused: Boolean = false,
            val showAddManagementButton: Boolean = false,

            // Adjective/Adverb-specific
            val comparative: String = "",
            val superlative: String = "",

            // UI hints
            val isSaveButtonEnabled: Boolean = false,
            @param:StringRes val errorMessageId: Int? = null,
        ) : UiState
    }

    sealed interface UiEvent {
        data class UpdateText(val text: String) : UiEvent
        data class UpdatePartOfSpeech(val partOfSpeech: PartOfSpeech) : UiEvent

        /* Translations */
        data class UpdateCurrentTranslation(val translation: String) : UiEvent
        data object AddTranslation : UiEvent
        data class RemoveTranslation(val translation: String) : UiEvent
        data class OnTranslationFieldFocusChange(val focused: Boolean) : UiEvent

        /* Examples */
        data class UpdateCurrentExample(val example: String) : UiEvent
        data object AddExample : UiEvent
        data class RemoveExample(val example: String) : UiEvent
        data class OnExampleFieldFocusChange(val focused: Boolean) : UiEvent

        /* Noun-specific */
        data class UpdateNounGender(val gender: WordGender) : UiEvent
        data class UpdatePluralForm(val plural: String) : UiEvent

        /* Verb-specific */
        /* Conjugations */
        data class UpdateCurrentConjugation(val conjugation: String) : UiEvent
        data object AddConjugation : UiEvent
        data class RemoveConjugation(val conjugation: String) : UiEvent
        data class OnConjugationFieldFocusChange(val focused: Boolean) : UiEvent

        /* Management */
        data class UpdateCurrentManagement(val management: String) : UiEvent
        data object AddManagement : UiEvent
        data class RemoveManagement(val management: String) : UiEvent
        data class OnManagementFieldFocusChange(val focused: Boolean) : UiEvent

        /* Adjective/Adverb specific */
        data class UpdateComparative(val comparative: String) : UiEvent
        data class UpdateSuperlative(val superlative: String) : UiEvent

        data object SaveWord : UiEvent
    }

    sealed interface UiEffect {
        /**
         * Indicates that a word was successfully saved and the screen can be closed.
         */
        data object WordSaved : UiEffect

        /**
         * Requests the UI to surface a non-blocking error message.
         */
        data class ShowError(val message: String) : UiEffect
    }
}
