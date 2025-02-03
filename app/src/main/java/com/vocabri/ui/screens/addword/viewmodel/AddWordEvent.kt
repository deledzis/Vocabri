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

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.WordGender

sealed interface AddWordEvent {
    data class UpdateText(val text: String) : AddWordEvent
    data class UpdatePartOfSpeech(val partOfSpeech: PartOfSpeech) : AddWordEvent

    /* Translations */
    data class UpdateCurrentTranslation(val translation: String) : AddWordEvent
    data object AddTranslation : AddWordEvent
    data class RemoveTranslation(val translation: String) : AddWordEvent
    data class OnTranslationFieldFocusChange(val focused: Boolean) : AddWordEvent

    /* Examples */
    data class UpdateCurrentExample(val example: String) : AddWordEvent
    data object AddExample : AddWordEvent
    data class RemoveExample(val example: String) : AddWordEvent
    data class OnExampleFieldFocusChange(val focused: Boolean) : AddWordEvent

    /* Noun-specific */
    data class UpdateNounGender(val gender: WordGender) : AddWordEvent
    data class UpdatePluralForm(val plural: String) : AddWordEvent

    /* Verb-specific */
    /* Conjugations */
    data class UpdateCurrentConjugation(val conjugation: String) : AddWordEvent
    data object AddConjugation : AddWordEvent
    data class RemoveConjugation(val conjugation: String) : AddWordEvent
    data class OnConjugationFieldFocusChange(val focused: Boolean) : AddWordEvent

    /* Management */
    data class UpdateCurrentManagement(val management: String) : AddWordEvent
    data object AddManagement : AddWordEvent
    data class RemoveManagement(val management: String) : AddWordEvent
    data class OnManagementFieldFocusChange(val focused: Boolean) : AddWordEvent

    /* Adjective/Adverb specific */
    data class UpdateComparative(val comparative: String) : AddWordEvent
    data class UpdateSuperlative(val superlative: String) : AddWordEvent

    data object SaveWord : AddWordEvent
}
