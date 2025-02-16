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
package com.vocabri.domain.repository

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing Word data.
 */
interface WordRepository {

    fun observeWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): Flow<List<Word>>

    /**
     * Retrieves words by a specific part of speech from the data source.
     *
     * @param partOfSpeech The part of speech used to filter words.
     * @return A list of words matching the specified part of speech.
     */
    suspend fun getWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): List<Word>

    /**
     * Inserts a new word into the data source.
     */
    suspend fun insertWord(word: Word)

    /**
     * Deletes a word by its unique identifier.
     */
    suspend fun deleteWordById(id: String)
}
