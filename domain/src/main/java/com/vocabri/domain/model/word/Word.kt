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
package com.vocabri.domain.model.word

/**
 * A sealed class representing the base domain model for words.
 * Each part of speech extends this class with its own properties.
 */
sealed class Word {
    abstract val id: String
    abstract val text: String
    abstract val translations: List<Translation>
    abstract val examples: List<Example>

    data class Noun(
        override val id: String,
        override val text: String,
        override val translations: List<Translation>,
        override val examples: List<Example>,
        val gender: String, // TODO: use enum
        val pluralForm: String,
    ) : Word()

    data class Verb(
        override val id: String,
        override val text: String,
        override val translations: List<Translation>,
        override val examples: List<Example>,
        val conjugation: String,
        val tenseForms: String,
    ) : Word()

    data class Adjective(
        override val id: String,
        override val text: String,
        override val translations: List<Translation>,
        override val examples: List<Example>,
        val comparative: String?,
        val superlative: String?,
    ) : Word()

    data class Adverb(
        override val id: String,
        override val text: String,
        override val translations: List<Translation>,
        override val examples: List<Example>,
        val comparative: String?,
        val superlative: String?,
    ) : Word()
}

/**
 * Extension function that maps a sealed class Word to a PartOfSpeech enum.
 */
fun Word.toPartOfSpeech(): PartOfSpeech = when (this) {
    is Word.Noun -> PartOfSpeech.NOUN
    is Word.Verb -> PartOfSpeech.VERB
    is Word.Adjective -> PartOfSpeech.ADJECTIVE
    is Word.Adverb -> PartOfSpeech.ADVERB
}
