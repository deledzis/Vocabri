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
package com.vocabri.ui.screens.dictionary.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech

internal val PartOfSpeech.toTitleResId: Int
    @StringRes get() = when (this) {
        PartOfSpeech.NOUN -> R.string.nouns
        PartOfSpeech.VERB -> R.string.verbs
        PartOfSpeech.ADJECTIVE -> R.string.adjectives
        PartOfSpeech.ADVERB -> R.string.adverbs
        // TODO: add back later
//        PartOfSpeech.PHRASE -> R.string.phrases
        PartOfSpeech.ALL -> R.string.all_words
    }

internal val PartOfSpeech.toLabelResId: Int
    @StringRes get() = when (this) {
        PartOfSpeech.NOUN -> R.string.noun_label
        PartOfSpeech.VERB -> R.string.verb_label
        PartOfSpeech.ADJECTIVE -> R.string.adjective_label
        PartOfSpeech.ADVERB -> R.string.adverb_label
        PartOfSpeech.ALL -> error("PartOfSpeech.ALL must not be used as a label!")
    }

internal val PartOfSpeech.toSmallCircleColor: Color
    get() = when (this) {
        PartOfSpeech.ALL -> Color.Transparent
        PartOfSpeech.NOUN -> Color(0xFFD91656)
        PartOfSpeech.VERB -> Color(0xFF093C72)
        PartOfSpeech.ADJECTIVE -> Color(0xFFFFB200)
        PartOfSpeech.ADVERB -> Color(0xFF16D957)
    }
