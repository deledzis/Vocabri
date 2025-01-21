package com.vocabri.data.datasource.word

import com.vocabri.data.ExampleEntity
import com.vocabri.data.TranslationEntity
import com.vocabri.data.WordEntity

interface WordDataSource {
    suspend fun insertWord(word: WordEntity)
    suspend fun insertTranslation(translation: TranslationEntity)
    suspend fun insertExample(example: ExampleEntity)
    suspend fun getAllWords(): List<WordEntity>
    suspend fun getWordById(wordId: String): WordEntity?
    suspend fun getTranslationsByWordId(wordId: String): List<TranslationEntity>
    suspend fun getExamplesByWordId(wordId: String): List<ExampleEntity>
    suspend fun deleteWordById(wordId: String)
    suspend fun deleteTranslationsByWordId(wordId: String)
    suspend fun deleteExamplesByWordId(wordId: String)
}
