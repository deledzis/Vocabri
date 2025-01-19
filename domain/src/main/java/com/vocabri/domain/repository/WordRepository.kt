package com.vocabri.domain.repository

import com.vocabri.domain.model.word.Word

interface WordRepository {
    suspend fun saveWord(word: Word)
    suspend fun getWords(): List<Word>
    suspend fun getWordById(id: String): Word?
    suspend fun deleteWord(id: String)
}
