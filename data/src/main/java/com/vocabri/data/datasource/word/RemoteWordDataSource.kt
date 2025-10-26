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

/**
 * Defines the contract for remote word operations.
 */
interface RemoteWordDataSource {

    /**
     * Fetches words for the provided [partOfSpeech] from the remote backend.
     */
    suspend fun getWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): List<Word>

    /**
     * Creates a new [word] in the remote backend.
     */
    suspend fun insertWord(word: Word)

    /**
     * Updates an existing [word] in the remote backend.
     */
    suspend fun updateWord(word: Word)

    /**
     * Deletes the remote representation of a word with [wordId].
     */
    suspend fun deleteWord(wordId: String)
}
