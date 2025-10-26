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
package com.vocabri.domain.fake

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.toPartOfSpeech
import com.vocabri.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWordRepositoryImpl : WordRepository {
    private val words = mutableListOf<Word>()

    override fun observeWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): Flow<List<Word>> = flowOf(words)

    override suspend fun getWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): List<Word> =
        if (partOfSpeech == PartOfSpeech.ALL) words else words.filter { it.toPartOfSpeech() == partOfSpeech }

    override suspend fun insertWord(word: Word) {
        val wordExists = words
            .filter { it.toPartOfSpeech() == word.toPartOfSpeech() }
            .any { it.text.equals(word.text, ignoreCase = true) }
        if (wordExists) {
            error("Word with text '${word.text}' already exists")
        }
        words.add(word)
    }

    override suspend fun deleteWordById(id: String) {
        words.removeIf { it.id == id }
    }

    override suspend fun syncPendingOperations() {
        // No-op for fake implementation
    }
}
