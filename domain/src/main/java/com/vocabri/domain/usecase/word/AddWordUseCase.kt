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

import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository
import com.vocabri.logger.logger

/**
 * Use case for adding a new word to the repository.
 *
 * This class encapsulates the logic for inserting a word into the data layer,
 * ensuring proper error handling and logging.
 *
 * @param wordRepository The repository responsible for word data operations.
 */
class AddWordUseCase(private val wordRepository: WordRepository) {

    private val log = logger()

    /**
     * Adds a word to the repository.
     *
     * If the insertion fails, the exception is logged and rethrown for higher-level handling.
     *
     * @param word The word to be added.
     * @throws Exception If an error occurs during the insertion process.
     */
    suspend fun execute(word: Word) {
        log.i { "Executing AddWordUseCase with word: $word" }

        try {
            wordRepository.insertWord(word)
            log.i { "Word successfully inserted: ${word.text}" }
        } catch (e: Exception) {
            log.e(e) { "Error inserting word: ${word.text}" }
            throw e
        }
    }
}
