package com.vocabri.data.datasource.word

import com.vocabri.data.VocabriDatabase
import com.vocabri.data.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordLocalDataSource(database: VocabriDatabase) {

    private val wordQueries by lazy { database.wordQueries }

    suspend fun addWord(word: WordEntity) = withContext(Dispatchers.IO) {
        wordQueries.insertWord(
            id = word.id,
            text = word.text,
            translations = word.translations,
            examples = word.examples,
            partOfSpeech = word.partOfSpeech,
            notes = word.notes
        )
    }

    suspend fun getWords(): List<WordEntity> = withContext(Dispatchers.IO) {
        wordQueries.selectAll().executeAsList()
    }

    suspend fun getWordById(id: String): WordEntity? = withContext(Dispatchers.IO) {
        wordQueries.selectById(id).executeAsOneOrNull()
    }

    suspend fun deleteWord(id: String) = withContext(Dispatchers.IO) {
        wordQueries.deleteWord(id)
    }
}
