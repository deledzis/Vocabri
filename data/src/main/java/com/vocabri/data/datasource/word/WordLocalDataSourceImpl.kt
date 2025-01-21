package com.vocabri.data.datasource.word

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import com.vocabri.data.ExampleEntity
import com.vocabri.data.TranslationEntity
import com.vocabri.data.VocabriDatabase
import com.vocabri.data.WordEntity
import com.vocabri.domain.util.logger

/**
 * A data source implementation for [WordEntity], [TranslationEntity], [ExampleEntity]
 * which uses local DB as a source of data.
 *
 * Handles word-related CRUD operations and interacts with the [VocabriDatabase].
 */
class WordLocalDataSourceImpl(private val database: VocabriDatabase) : WordDataSource {

    private val log = logger()

    /**
     * Inserts a word entity into the database.
     */
    override suspend fun insertWord(word: WordEntity) {
        log.i { "Inserting word: ${word.text}" }
        database.wordQueries.insertWord(
            id = word.id,
            text = word.text,
            partOfSpeech = word.partOfSpeech,
            notes = word.notes
        )
        log.i { "Word inserted with ID: ${word.id}" }
    }

    /**
     * Inserts a translation entity into the database.
     */
    override suspend fun insertTranslation(translation: TranslationEntity) {
        log.i { "Inserting translation: ${translation.translation} for word ID: ${translation.wordId}" }
        database.wordQueries.insertTranslation(
            id = translation.id,
            wordId = translation.wordId,
            translation = translation.translation
        )
        log.i { "Translation inserted with ID: ${translation.id}" }
    }

    /**
     * Inserts an example entity into the database.
     */
    override suspend fun insertExample(example: ExampleEntity) {
        log.i { "Inserting example: ${example.example} for word ID: ${example.wordId}" }
        database.wordQueries.insertExample(
            id = example.id,
            wordId = example.wordId,
            example = example.example
        )
        log.i { "Example inserted with ID: ${example.id}" }
    }

    /**
     * Retrieves all word entities from the database.
     */
    override suspend fun getAllWords(): List<WordEntity> {
        log.i { "Fetching all words from the database" }
        val words = database.wordQueries.getAllWords { id, text, partOfSpeech, notes ->
            WordEntity(
                id = id,
                text = text,
                partOfSpeech = partOfSpeech,
                notes = notes
            )
        }.awaitAsList()
        log.i { "Retrieved ${words.size} words from the database: $words" }
        return words
    }

    /**
     * Retrieves a word entity by its ID.
     */
    override suspend fun getWordById(wordId: String): WordEntity? {
        log.i { "Fetching word with ID: $wordId" }
        val word = database.wordQueries.getWordById(wordId).awaitAsOneOrNull()
        log.i { if (word != null) "Word found with ID: $wordId" else "No word found with ID: $wordId" }
        return word
    }

    /**
     * Retrieves all translations for a given word ID.
     */
    override suspend fun getTranslationsByWordId(wordId: String): List<TranslationEntity> {
        log.i { "Fetching translations for word ID: $wordId" }
        val translations =
            database.wordQueries.getTranslationsByWordId(wordId) { id, newWordId, translation ->
                TranslationEntity(
                    id = id,
                    wordId = newWordId,
                    translation = translation
                )
            }.awaitAsList()
        log.i { "Retrieved ${translations.size} translations for word ID: $wordId" }
        return translations
    }

    /**
     * Retrieves all examples for a given word ID.
     */
    override suspend fun getExamplesByWordId(wordId: String): List<ExampleEntity> {
        log.i { "Fetching examples for word ID: $wordId" }
        val examples = database.wordQueries.getExamplesByWordId(wordId) { id, newWordId, example ->
            ExampleEntity(
                id = id,
                wordId = newWordId,
                example = example
            )
        }.awaitAsList()
        log.i { "Retrieved ${examples.size} examples for word ID: $wordId" }
        return examples
    }

    /**
     * Deletes a word entity by its ID.
     */
    override suspend fun deleteWordById(wordId: String) {
        log.i { "Deleting word with ID: $wordId" }
        database.wordQueries.deleteWordById(wordId)
        log.i { "Word with ID $wordId deleted" }
    }

    /**
     * Deletes all translations for a given word ID.
     */
    override suspend fun deleteTranslationsByWordId(wordId: String) {
        log.i { "Deleting translations for word ID: $wordId" }
        database.wordQueries.deleteTranslationsByWordId(wordId)
        log.i { "Translations for word ID $wordId deleted" }
    }

    /**
     * Deletes all examples for a given word ID.
     */
    override suspend fun deleteExamplesByWordId(wordId: String) {
        log.i { "Deleting examples for word ID: $wordId" }
        database.wordQueries.deleteExamplesByWordId(wordId)
        log.i { "Examples for word ID $wordId deleted" }
    }
}
