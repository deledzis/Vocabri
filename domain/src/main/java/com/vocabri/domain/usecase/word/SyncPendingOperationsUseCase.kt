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
 * Use case for synchronizing pending operations with the remote data source.
 *
 * This should be called when network connectivity is available to sync
 * operations that previously failed (e.g., when the device was offline).
 *
 * @param wordRepository The repository responsible for word data operations.
 */
class SyncPendingOperationsUseCase(private val wordRepository: WordRepository) {

    private val log = logger()

    /**
     * Synchronizes pending operations with the remote data source.
     *
     * This method attempts to replay queued operations (INSERT, DELETE, UPDATE)
     * to the remote backend. Successfully synced operations are removed from the queue.
     */
    suspend fun execute() {
        log.i { "Executing SyncPendingOperationsUseCase" }
        wordRepository.syncPendingOperations()
        log.i { "SyncPendingOperationsUseCase completed" }
    }
}
