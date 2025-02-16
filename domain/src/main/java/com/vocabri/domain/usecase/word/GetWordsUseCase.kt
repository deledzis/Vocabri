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
package com.vocabri.domain.usecase.word

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository
import com.vocabri.logger.logger

/**
 * Use case for fetching words.
 */
open class GetWordsUseCase(private val wordRepository: WordRepository) {

    private val log = logger()

    /**
     * Fetches words by part of speech.
     *
     * @param partOfSpeech The part of speech to filter words by.
     * @return List of words for the specified part of speech.
     */
    open suspend fun execute(partOfSpeech: PartOfSpeech): List<Word> {
        log.i { "Executing GetWordsUseCase to fetch words by partOfSpeech: $partOfSpeech" }
        return wordRepository.getWordsByPartOfSpeech(partOfSpeech).also {
            log.i { "Found ${it.size} words with partOfSpeech = $partOfSpeech" }
        }
    }
}
