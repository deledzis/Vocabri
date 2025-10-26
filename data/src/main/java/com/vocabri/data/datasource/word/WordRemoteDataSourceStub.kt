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
import com.vocabri.logger.logger
import io.ktor.client.HttpClient

/**
 * Stub implementation of [RemoteWordDataSource] that will be replaced once backend APIs are ready.
 */
@Suppress("UnusedPrivateMember")
class WordRemoteDataSourceStub(private val httpClient: HttpClient) : RemoteWordDataSource {

    private val log = logger()

    override suspend fun getWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): List<Word> {
        log.i { "Stub remote getWordsByPartOfSpeech for $partOfSpeech" }
        // TODO: Replace stub with actual remote API call when backend endpoints are available.
        return emptyList()
    }

    override suspend fun insertWord(word: Word) {
        log.i { "Stub remote insertWord for wordId=${word.id}" }
        // TODO: Replace stub with actual remote API call when backend endpoints are available.
    }

    override suspend fun updateWord(word: Word) {
        log.i { "Stub remote updateWord for wordId=${word.id}" }
        // TODO: Replace stub with actual remote API call when backend endpoints are available.
    }

    override suspend fun deleteWord(wordId: String) {
        log.i { "Stub remote deleteWord for wordId=$wordId" }
        // TODO: Replace stub with actual remote API call when backend endpoints are available.
    }
}
