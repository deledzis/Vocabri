package com.vocabri.data.repository.word

import com.vocabri.data.datasource.word.WordLocalDataSource
import com.vocabri.data.mapper.WordMapper
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository

class WordRepositoryImpl(
    private val localDataSource: WordLocalDataSource,
    private val mapper: WordMapper
) : WordRepository {
    override suspend fun saveWord(word: Word) {
        localDataSource.addWord(
            mapper.toDatabaseModel(word)
        )
    }

    override suspend fun getWords(): List<Word> = localDataSource.getWords().map { entity ->
        mapper.toDomainModel(entity)
    }

    override suspend fun getWordById(id: String): Word? =
        localDataSource.getWordById(id)?.let { entity ->
            mapper.toDomainModel(entity)
        }

    override suspend fun deleteWord(id: String) {
        localDataSource.deleteWord(id)
    }
}

