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
package com.vocabri.data.repository.word

import com.vocabri.data.ExampleEntity
import com.vocabri.data.TranslationEntity
import com.vocabri.data.WordEntity
import com.vocabri.data.datasource.word.WordDataSource
import com.vocabri.domain.model.word.Example
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository
import com.vocabri.logger.logger
import com.vocabri.utils.IdGenerator

/**
 * Implementation of [WordRepository] that manages word data using a local data source.
 *
 * This class handles operations such as retrieving, inserting, and deleting words,
 * including their translations and examples.
 */
class WordRepositoryImpl(private val localDataSource: WordDataSource, private val idGenerator: IdGenerator) :
    WordRepository {

    private val log = logger()

    /**
     * Retrieves all words from the local data source, including their translations and examples.
     *
     * @return A list of [Word] objects.
     */
    override suspend fun getAllWords(): List<Word> {
        log.i { "Fetching all words from the local data source" }
        val wordEntities = localDataSource.getAllWords()
        log.i { "Retrieved ${wordEntities.size} words from the local data source" }

        return wordEntities.map { wordEntity ->
            Word(
                id = wordEntity.id,
                text = wordEntity.text,
                partOfSpeech = PartOfSpeech.valueOf(wordEntity.partOfSpeech),
                notes = wordEntity.notes,
                translations = localDataSource.getTranslationsByWordId(wordEntity.id)
                    .map { translationEntity ->
                        Translation(
                            id = translationEntity.id,
                            text = translationEntity.translation,
                        )
                    },
                examples = localDataSource.getExamplesByWordId(wordEntity.id).map { exampleEntity ->
                    Example(
                        id = exampleEntity.id,
                        text = exampleEntity.example,
                    )
                },
            )
        }
    }

    /**
     * Retrieves a specific word by its ID, including its translations and examples.
     *
     * @param id The ID of the word to retrieve.
     * @return The [Word] object if found, or null if no word exists with the given ID.
     */
    override suspend fun getWordById(id: String): Word? {
        log.i { "Fetching word with ID: $id" }
        val wordEntity = localDataSource.getWordById(id) ?: return null

        val translations = localDataSource.getTranslationsByWordId(id).map { translationEntity ->
            Translation(
                id = translationEntity.id,
                text = translationEntity.translation,
            )
        }
        val examples = localDataSource.getExamplesByWordId(id).map { exampleEntity ->
            Example(
                id = exampleEntity.id,
                text = exampleEntity.example,
            )
        }

        log.i { "Successfully retrieved word with ID: $id" }
        return Word(
            id = wordEntity.id,
            text = wordEntity.text,
            partOfSpeech = PartOfSpeech.valueOf(wordEntity.partOfSpeech),
            notes = wordEntity.notes,
            translations = translations,
            examples = examples,
        )
    }

    /**
     * Inserts a new word into the local data source.
     *
     * @param word The [Word] to insert.
     * @throws IllegalArgumentException If a word with the same text already exists.
     */
    override suspend fun insertWord(word: Word) {
        log.i { "Inserting word: ${word.text}" }

        val existingWords = localDataSource.getAllWords().filter { it.text == word.text }
        if (existingWords.isNotEmpty()) {
            log.w { "Word with text '${word.text}' already exists" }
            throw IllegalArgumentException("Word with text '${word.text}' already exists")
        }

        val wordEntity = WordEntity(
            id = word.id,
            text = word.text,
            partOfSpeech = word.partOfSpeech.name,
            notes = word.notes,
        )
        localDataSource.insertWord(wordEntity)
        log.i { "Inserted word entity: ${wordEntity.text}" }

        word.translations.forEach { translation ->
            val translationEntity = TranslationEntity(
                id = idGenerator.generateStringId(),
                wordId = word.id,
                translation = translation.text,
            )
            localDataSource.insertTranslation(translationEntity)
        }
        log.i { "Inserted ${word.translations.size} translations for word: ${word.text}" }

        word.examples.forEach { example ->
            val exampleEntity = ExampleEntity(
                id = idGenerator.generateStringId(),
                wordId = word.id,
                example = example.text,
            )
            localDataSource.insertExample(exampleEntity)
        }
        log.i { "Inserted ${word.examples.size} examples for word: ${word.text}" }
    }

    /**
     * Deletes a word and its associated translations and examples from the local data source.
     *
     * @param id The ID of the word to delete.
     */
    override suspend fun deleteWordById(id: String) {
        log.i { "Deleting word, its translation and examples with ID: $id" }

        localDataSource.deleteWordById(id)
        localDataSource.deleteTranslationsByWordId(id)
        localDataSource.deleteExamplesByWordId(id)
    }
}
