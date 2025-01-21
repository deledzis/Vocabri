package com.vocabri.domain.repository

import com.vocabri.domain.model.word.Word

interface WordRepository {
    suspend fun getAllWords(): List<Word>
    suspend fun getWordById(id: String): Word?
    suspend fun insertWord(word: Word)
    suspend fun deleteWordById(id: String)
}
