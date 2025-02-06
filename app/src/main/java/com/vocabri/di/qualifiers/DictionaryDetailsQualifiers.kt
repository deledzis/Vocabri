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
package com.vocabri.di.qualifiers

import com.vocabri.domain.model.word.PartOfSpeech
import org.koin.core.qualifier.StringQualifier

object DictionaryDetailsQualifiers {
    private val DICTIONARY_DETAILS_ALL_WORDS = StringQualifier("DICTIONARY_DETAILS_ALL_WORDS")
    private val DICTIONARY_DETAILS_NOUNS = StringQualifier("DICTIONARY_DETAILS_NOUNS")
    private val DICTIONARY_DETAILS_VERBS = StringQualifier("DICTIONARY_DETAILS_VERBS")
    private val DICTIONARY_DETAILS_ADJECTIVES = StringQualifier("DICTIONARY_DETAILS_ADJECTIVES")
    private val DICTIONARY_DETAILS_ADVERBS = StringQualifier("DICTIONARY_DETAILS_ADVERBS")

    fun fromPartOfSpeech(partOfSpeech: PartOfSpeech) = when (partOfSpeech) {
        PartOfSpeech.ALL -> DICTIONARY_DETAILS_ALL_WORDS
        PartOfSpeech.NOUN -> DICTIONARY_DETAILS_NOUNS
        PartOfSpeech.VERB -> DICTIONARY_DETAILS_VERBS
        PartOfSpeech.ADJECTIVE -> DICTIONARY_DETAILS_ADJECTIVES
        PartOfSpeech.ADVERB -> DICTIONARY_DETAILS_ADVERBS
    }
}
