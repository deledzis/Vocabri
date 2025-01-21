package com.vocabri.domain.fake

import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository

class FakeWordRepository : WordRepository {
    private val words = mutableListOf<Word>()

    override suspend fun getAllWords(): List<Word> = words

    override suspend fun getWordById(id: String): Word = words.first { it.id == id }

    override suspend fun insertWord(word: Word) {
        if (words.any { it.text.equals(word.text, ignoreCase = true) }) {
            throw IllegalArgumentException("Word with text '${word.text}' already exists")
        }
        words.add(word)
    }

    override suspend fun deleteWordById(id: String) {
        words.removeIf { it.id == id }
    }
}
