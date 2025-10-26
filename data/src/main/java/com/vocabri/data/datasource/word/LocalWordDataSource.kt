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
package com.vocabri.data.datasource.word

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for local word data operations.
 */
interface LocalWordDataSource {

    /**
     * Retrieves a list of words filtered by the given part of speech.
     * If partOfSpeech is null, then all words should be returned.
     */
    suspend fun getWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): List<Word>

    fun observeWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): Flow<List<Word>>

    /**
     * Inserts a new word (and related entities) into the database.
     */
    suspend fun insertWord(word: Word)

    /**
     * Deletes a word by its ID.
     */
    suspend fun deleteWord(wordId: String)
}
