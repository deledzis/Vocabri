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

import com.vocabri.data.datasource.sync.OperationType
import com.vocabri.data.datasource.sync.PendingOperation
import com.vocabri.data.datasource.sync.PendingOperationDataSource
import com.vocabri.data.datasource.word.LocalWordDataSource
import com.vocabri.data.datasource.word.RemoteWordDataSource
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.toPartOfSpeech
import com.vocabri.domain.repository.WordRepository
import com.vocabri.logger.logger
import com.vocabri.utils.IdGenerator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [WordRepository] that orchestrates remote and local data sources.
 */
class WordRepositoryImpl(
    private val localWordDataSource: LocalWordDataSource,
    private val remoteWordDataSource: RemoteWordDataSource,
    private val pendingOperationDataSource: PendingOperationDataSource,
    private val idGenerator: IdGenerator,
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
            log.e { "Word with text '${word.text}' already exists" }
            error("Word with text '${word.text}' already exists")
        }

        localWordDataSource.insertWord(word)
        log.i { "Word inserted to local database, ID = ${word.id}" }

        attemptRemoteOperationWithFallback(
            operation = "insert for wordId=${word.id}",
            operationType = OperationType.INSERT,
            wordId = word.id,
            wordData = word,
        ) {
            remoteWordDataSource.insertWord(word)
        }

        log.i { "Word insert operation completed, ID = ${word.id}" }
    }

    override suspend fun deleteWordById(id: String) {
        log.i { "deleteWordById called for ID = $id" }

        localWordDataSource.deleteWord(id)
        log.i { "Word with ID = $id deleted from local database" }

        attemptRemoteOperationWithFallback(
            operation = "delete for wordId=$id",
            operationType = OperationType.DELETE,
            wordId = id,
            wordData = null,
        ) {
            remoteWordDataSource.deleteWord(id)
        }

        log.i { "Word delete operation completed, ID = $id" }
    }

    override suspend fun syncPendingOperations() {
        log.i { "Starting sync of pending operations" }

        val pendingOperations = pendingOperationDataSource.getAllPendingOperations()
        log.i { "Found ${pendingOperations.size} pending operations to sync" }

        pendingOperations.forEach { operation ->
            val success = runCatching {
                when (operation.operationType) {
                    OperationType.INSERT -> {
                        operation.wordData?.let { word ->
                            log.i { "Syncing INSERT operation for wordId=${operation.wordId}" }
                            remoteWordDataSource.insertWord(word)
                        } ?: log.w { "INSERT operation missing word data for wordId=${operation.wordId}" }
                    }

                    OperationType.DELETE -> {
                        log.i { "Syncing DELETE operation for wordId=${operation.wordId}" }
                        remoteWordDataSource.deleteWord(operation.wordId)
                    }

                    OperationType.UPDATE -> {
                        operation.wordData?.let { word ->
                            log.i { "Syncing UPDATE operation for wordId=${operation.wordId}" }
                            remoteWordDataSource.updateWord(word)
                        } ?: log.w { "UPDATE operation missing word data for wordId=${operation.wordId}" }
                    }
                }
                true
            }.onFailure { throwable ->
                val errorMsg = throwable.message ?: throwable::class.simpleName
                log.w { "Failed to sync operation ${operation.id}: $errorMsg" }
            }.getOrDefault(false)

            if (success) {
                pendingOperationDataSource.deletePendingOperation(operation.id)
                log.i { "Successfully synced and removed operation ${operation.id}" }
            }
        }

        log.i { "Sync of pending operations completed" }
    }

    private suspend fun attemptRemoteOperationWithFallback(
        operation: String,
        operationType: OperationType,
        wordId: String,
        wordData: Word?,
        block: suspend () -> Unit,
    ) {
        log.i { "Attempting remote $operation" }
        try {
            block()
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            log.w { "Remote $operation failed: ${throwable.message ?: throwable::class.simpleName}" }
            log.i { "Queueing operation for later sync" }
            val pendingOperation = PendingOperation(
                id = idGenerator.generateStringId(),
                operationType = operationType,
                wordId = wordId,
                wordData = wordData,
                timestamp = System.currentTimeMillis(),
            )
            pendingOperationDataSource.insertPendingOperation(pendingOperation)
        }
    }
}
