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

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.vocabri.data.db.VocabriDatabase
import com.vocabri.domain.model.word.Example
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.WordGender
import com.vocabri.logger.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Implementation of the WordDataSource interface using SQLDelight for local storage.
 * All queries for the different parts of speech are located in database.wordQueries.
 */
class WordLocalDataSourceImpl(private val database: VocabriDatabase) : WordDataSource {

    private val log = logger()

    /**
     * Inserts a new word into the database, along with part-of-speech-specific data.
     */
    override suspend fun insertWord(word: Word) {
        log.i { "Starting insertWord for Word ID = ${word.id}, text = ${word.text}" }

        try {
            // Insert into WordBaseEntity
            database.wordQueries.insertWord(
                id = word.id,
                text = word.text,
            )
            log.i { "Inserted base entity for Word ID = ${word.id}" }
        } catch (e: Exception) {
            log.e { "Error inserting base entity for Word ID = ${word.id}: ${e.message}" }
            throw e
        }

        // Insert into the specific table, depending on the type of Word
        when (word) {
            is Word.Noun -> {
                try {
                    database.wordQueries.insertNoun(
                        id = word.id,
                        gender = word.gender.toString(),
                        pluralForm = word.pluralForm.orEmpty(),
                    )
                    log.i { "Inserted noun entity for Word ID = ${word.id}" }
                } catch (e: Exception) {
                    log.e { "Error inserting noun entity for Word ID = ${word.id}: ${e.message}" }
                    throw e
                }
            }

            is Word.Verb -> {
                try {
                    database.wordQueries.insertVerb(
                        id = word.id,
                        conjugation = word.conjugation.orEmpty(),
                        management = word.management.orEmpty(),
                    )
                    log.i { "Inserted verb entity for Word ID = ${word.id}" }
                } catch (e: Exception) {
                    log.e { "Error inserting verb entity for Word ID = ${word.id}: ${e.message}" }
                    throw e
                }
            }

            is Word.Adjective -> {
                try {
                    database.wordQueries.insertAdjective(
                        id = word.id,
                        comparative = word.comparative,
                        superlative = word.superlative,
                    )
                    log.i { "Inserted adjective entity for Word ID = ${word.id}" }
                } catch (e: Exception) {
                    log.e { "Error inserting adjective entity for Word ID = ${word.id}: ${e.message}" }
                    throw e
                }
            }

            is Word.Adverb -> {
                try {
                    database.wordQueries.insertAdverb(
                        id = word.id,
                        comparative = word.comparative,
                        superlative = word.superlative,
                    )
                    log.i { "Inserted adverb entity for Word ID = ${word.id}" }
                } catch (e: Exception) {
                    log.e { "Error inserting adverb entity for Word ID = ${word.id}: ${e.message}" }
                    throw e
                }
            }
        }

        // Insert translations
        word.translations.forEach { translation ->
            try {
                database.wordQueries.insertTranslation(
                    id = translation.id,
                    wordId = word.id,
                    translation = translation.translation,
                )
                log.i { "Inserted translation ID = ${translation.id} for Word ID = ${word.id}" }
            } catch (e: Exception) {
                log.e { "Error inserting translation ID = ${translation.id} for Word ID = ${word.id}: ${e.message}" }
                throw e
            }
        }

        // Insert examples
        word.examples.forEach { example ->
            try {
                database.wordQueries.insertExample(
                    id = example.id,
                    wordId = word.id,
                    example = example.example,
                )
                log.i { "Inserted example ID = ${example.id} for Word ID = ${word.id}" }
            } catch (e: Exception) {
                log.e { "Error inserting example ID = ${example.id} for Word ID = ${word.id}: ${e.message}" }
                throw e
            }
        }

        log.i { "Finished inserting word with ID = ${word.id}" }
    }

    /**
     * Retrieves words by part of speech.
     */
    override suspend fun getWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): List<Word> {
        log.i { "getWordsByPartOfSpeech invoked with partOfSpeech = $partOfSpeech" }
        return observeWordsByPartOfSpeech(partOfSpeech).first()
    }

    /**
     * Returns a reactive flow of words filtered by partOfSpeech.
     */
    override fun observeWordsByPartOfSpeech(partOfSpeech: PartOfSpeech): Flow<List<Word>> = database.wordQueries
        .selectAllWords()
        .asFlow()
        .mapToList(context = Dispatchers.IO)
        .map { baseWords ->
            log.i { "Base words updated. Count = ${baseWords.size}" }

            val domainWords = baseWords.map { wordBase ->
                // 1) Fetch translations
                val translations = database.wordQueries
                    .selectTranslationsByWordId(wordBase.id)
                    .executeAsList()
                    .map { Translation(id = it.id, translation = it.translation) }

                // 2) Fetch examples
                val examples = database.wordQueries
                    .selectExamplesByWordId(wordBase.id)
                    .executeAsList()
                    .map { Example(id = it.id, example = it.example) }

                // 3) Check which specialized entity exists
                val nounEntity = database.wordQueries.selectNounById(wordBase.id).executeAsOneOrNull()
                if (nounEntity != null) {
                    return@map Word.Noun(
                        id = wordBase.id,
                        text = wordBase.text,
                        translations = translations,
                        examples = examples,
                        gender = WordGender.fromString(nounEntity.gender),
                        pluralForm = nounEntity.pluralForm,
                    )
                }

                val verbEntity = database.wordQueries.selectVerbById(wordBase.id).executeAsOneOrNull()
                if (verbEntity != null) {
                    return@map Word.Verb(
                        id = wordBase.id,
                        text = wordBase.text,
                        translations = translations,
                        examples = examples,
                        conjugation = verbEntity.conjugation,
                        management = verbEntity.management,
                    )
                }

                val adjEntity = database.wordQueries.selectAdjectiveById(wordBase.id).executeAsOneOrNull()
                if (adjEntity != null) {
                    return@map Word.Adjective(
                        id = wordBase.id,
                        text = wordBase.text,
                        translations = translations,
                        examples = examples,
                        comparative = adjEntity.comparative,
                        superlative = adjEntity.superlative,
                    )
                }

                val advEntity = database.wordQueries.selectAdverbById(wordBase.id).executeAsOneOrNull()
                if (advEntity != null) {
                    return@map Word.Adverb(
                        id = wordBase.id,
                        text = wordBase.text,
                        translations = translations,
                        examples = examples,
                        comparative = advEntity.comparative,
                        superlative = advEntity.superlative,
                    )
                }

                // If we get here, we have an unknown POS
                error("Unknown part of speech for word with ID = ${wordBase.id}")
            }

            // 4) Filter by partOfSpeech if needed
            if (partOfSpeech == PartOfSpeech.ALL) {
                log.i { "Returning ALL domain words. Count = ${domainWords.size}" }
                domainWords
            } else {
                val filtered = domainWords.filter { word ->
                    when (word) {
                        is Word.Noun -> partOfSpeech == PartOfSpeech.NOUN
                        is Word.Verb -> partOfSpeech == PartOfSpeech.VERB
                        is Word.Adjective -> partOfSpeech == PartOfSpeech.ADJECTIVE
                        is Word.Adverb -> partOfSpeech == PartOfSpeech.ADVERB
                    }
                }
                log.i { "Returning FILTERED words. Count = ${filtered.size}" }
                filtered
            }
        }

    /**
     * Deletes a word and all related records from the database.
     * With ON DELETE CASCADE in the schema, removing from WordBaseEntity
     * should remove all related records automatically.
     */
    override suspend fun deleteWord(wordId: String) {
        log.i { "Deleting word by ID = $wordId" }
        database.wordQueries.deleteWordById(wordId)
        log.i { "Word with ID = $wordId deleted successfully" }
    }
}
