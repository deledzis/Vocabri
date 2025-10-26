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

import com.vocabri.data.datasource.word.RemoteWordDataSource
import com.vocabri.data.datasource.word.WordDataSource
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.toPartOfSpeech
import com.vocabri.domain.repository.WordRepository
import com.vocabri.logger.logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [WordRepository] that orchestrates remote and local data sources.
 */
class WordRepositoryImpl(
    private val localWordDataSource: WordDataSource,
    private val remoteWordDataSource: RemoteWordDataSource,
) : WordRepository {

    private val log = logger()

    override fun observeWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): Flow<List<Word>> {
        log.i { "observeWordsByPartOfSpeech called for $partOfSpeech" }
        return localWordDataSource.observeWordsByPartOfSpeech(partOfSpeech)
    }

    override suspend fun getWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): List<Word> {
        log.i { "getWordsByPartOfSpeech called for $partOfSpeech" }
        val words = localWordDataSource.getWordsByPartOfSpeech(partOfSpeech)
        log.i { "Fetched ${words.size} words for $partOfSpeech" }
        return words
    }

    override suspend fun insertWord(word: Word) {
        log.i { "insertWord called with ID = ${word.id}, text = ${word.text}" }

        val existingWords = localWordDataSource
            .getWordsByPartOfSpeech(word.toPartOfSpeech())
            .filter { it.text == word.text }
        if (existingWords.isNotEmpty()) {
            log.w { "Word with text '${word.text}' already exists" }
            error("Word with text '${word.text}' already exists")
        }

        attemptRemoteOperation("insert for wordId=${word.id}") {
            remoteWordDataSource.insertWord(word)
        }

        localWordDataSource.insertWord(word)
        log.i { "Word inserted successfully, ID = ${word.id}" }
    }

    override suspend fun deleteWordById(id: String) {
        log.i { "deleteWordById called for ID = $id" }

        attemptRemoteOperation("delete for wordId=$id") {
            remoteWordDataSource.deleteWord(id)
        }

        localWordDataSource.deleteWord(id)
        log.i { "Word with ID = $id deleted successfully" }
    }

    private suspend fun attemptRemoteOperation(operation: String, block: suspend () -> Unit) {
        log.i { "Attempting remote $operation" }
        try {
            block()
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            log.w { "Remote $operation failed: ${throwable.message ?: throwable::class.simpleName}" }
        }
    }
}
