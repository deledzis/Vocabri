package com.vocabri.domain.fake

import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository

class FakeWordRepository : WordRepository {
    private val words = mutableListOf<Word>()

    override suspend fun saveWord(word: Word) {
        words.add(word)
    }

    override suspend fun getWords(): List<Word> = words

    override suspend fun getWordById(id: String): Word? = words.find { it.id == id }

    override suspend fun deleteWord(id: String) {
        words.removeIf { it.id == id }
    }
}
