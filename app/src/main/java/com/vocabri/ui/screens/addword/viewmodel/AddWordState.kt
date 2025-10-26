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

sealed interface AddWordState {
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
    ) : AddWordState
}
