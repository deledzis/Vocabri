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

import com.vocabri.domain.repository.WordRepository
import com.vocabri.logger.logger

/**
 * Use case for deleting a word from the repository.
 *
 * This class handles the logic for removing a word by its ID from the data layer.
 *
 * @param wordRepository The repository responsible for word data operations.
 */
class DeleteWordUseCase(private val wordRepository: WordRepository) {

    private val log = logger()

    /**
     * Deletes a word by its ID.
     *
     * @param id The unique identifier of the word to be deleted.
     * @throws Exception If an error occurs during the deletion process.
     */
    suspend fun execute(id: String) {
        log.i { "Executing DeleteWordUseCase for word ID: $id" }
        wordRepository.deleteWordById(id)
        log.i { "Word with ID $id successfully deleted" }
    }
}
