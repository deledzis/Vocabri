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
package com.vocabri.data.repository.word

import com.vocabri.data.datasource.word.WordDataSource
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository
import com.vocabri.logger.logger

/**
 * Implementation of the WordsRepository interface that delegates to WordDataSource.
 */
class WordRepositoryImpl(private val wordDataSource: WordDataSource) : WordRepository {

    private val log = logger()

    /**
     * Retrieves all words from the data source.
     */
    override suspend fun getAllWords(): List<Word> {
        log.i { "getAllWords called" }
        // Passing null to getWordsByPartOfSpeech means "fetch all words".
        val words = wordDataSource.getWordsByPartOfSpeech(partOfSpeech = null)
        log.i { "Fetched ${words.size} words in total" }
        return words
    }

    /**
     * Retrieves words by a specific part of speech.
     */
    override suspend fun getWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): List<Word> {
        log.i { "getWordsByPartOfSpeech called for $partOfSpeech" }
        val words = wordDataSource.getWordsByPartOfSpeech(partOfSpeech)
        log.i { "Fetched ${words.size} words for $partOfSpeech" }
        return words
    }

    /**
     * Inserts a word by delegating to the local data source.
     */
    override suspend fun insertWord(word: Word) {
        log.i { "insertWord called with ID = ${word.id}, text = ${word.text}" }

        // Passing null to getWordsByPartOfSpeech means "fetch all words".
        val existingWords = wordDataSource.getWordsByPartOfSpeech(null).filter { it.text == word.text }
        if (existingWords.isNotEmpty()) {
            log.w { "Word with text '${word.text}' already exists" }
            throw IllegalArgumentException("Word with text '${word.text}' already exists")
        }

        wordDataSource.insertWord(word)
        log.i { "Word inserted successfully, ID = ${word.id}" }
    }

    /**
     * Deletes a word by its ID, leveraging the local data source.
     */
    override suspend fun deleteWordById(id: String) {
        log.i { "deleteWordById called for ID = $id" }
        wordDataSource.deleteWord(id)
        log.i { "Word with ID = $id deleted successfully" }
    }
}
