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
package com.vocabri.data.datasource.sync

import android.util.Base64
import com.vocabri.data.db.VocabriDatabase
import com.vocabri.domain.model.word.Example
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.WordGender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PendingOperationLocalDataSource(private val database: VocabriDatabase) : PendingOperationDataSource {

    private companion object {
        const val PART_SEPARATOR = ";"
        const val ITEM_SEPARATOR = "|"
        const val FIELD_SEPARATOR = ":"
        const val WORD_TYPE_INDEX = 0
        const val WORD_ID_INDEX = 1
        const val WORD_TEXT_INDEX = 2
        const val TRANSLATIONS_INDEX = 3
        const val EXAMPLES_INDEX = 4
        const val EXTRA_FIELD_1_INDEX = 5
        const val EXTRA_FIELD_2_INDEX = 6
    }

    override suspend fun insertPendingOperation(operation: PendingOperation): Unit = withContext(Dispatchers.IO) {
        val wordDataJson = operation.wordData?.let { serializeWord(it) }
        database.wordQueries.insertPendingOperation(
            id = operation.id,
            operationType = operation.operationType.name,
            wordId = operation.wordId,
            wordData = wordDataJson,
            timestamp = operation.timestamp,
        )
    }

    override suspend fun getAllPendingOperations(): List<PendingOperation> = withContext(Dispatchers.IO) {
        database.wordQueries.selectAllPendingOperations().executeAsList().map { entity ->
            PendingOperation(
                id = entity.id,
                operationType = OperationType.valueOf(entity.operationType),
                wordId = entity.wordId,
                wordData = entity.wordData?.let { deserializeWord(it) },
                timestamp = entity.timestamp,
            )
        }
    }

    override suspend fun deletePendingOperation(id: String): Unit = withContext(Dispatchers.IO) {
        database.wordQueries.deletePendingOperationById(id)
    }

    override suspend fun deleteAllPendingOperations(): Unit = withContext(Dispatchers.IO) {
        database.wordQueries.deleteAllPendingOperations()
    }

    private fun serializeWord(word: Word): String {
        val translations = word.translations.joinToString(ITEM_SEPARATOR) {
            "${it.id}$FIELD_SEPARATOR${encodeBase64(it.translation)}"
        }
        val examples = word.examples.joinToString(ITEM_SEPARATOR) {
            "${it.id}$FIELD_SEPARATOR${encodeBase64(it.example)}"
        }

        return when (word) {
            is Word.Noun ->
                "NOUN$PART_SEPARATOR${word.id}$PART_SEPARATOR" +
                    "${encodeBase64(word.text)}$PART_SEPARATOR$translations$PART_SEPARATOR" +
                    "$examples$PART_SEPARATOR${word.gender?.name ?: ""}$PART_SEPARATOR" +
                    encodeBase64(word.pluralForm ?: "")
            is Word.Verb ->
                "VERB$PART_SEPARATOR${word.id}$PART_SEPARATOR" +
                    "${encodeBase64(word.text)}$PART_SEPARATOR$translations$PART_SEPARATOR" +
                    "$examples$PART_SEPARATOR${encodeBase64(word.conjugation ?: "")}$PART_SEPARATOR" +
                    encodeBase64(word.management ?: "")
            is Word.Adjective ->
                "ADJECTIVE$PART_SEPARATOR${word.id}$PART_SEPARATOR" +
                    "${encodeBase64(word.text)}$PART_SEPARATOR$translations$PART_SEPARATOR" +
                    "$examples$PART_SEPARATOR${encodeBase64(word.comparative ?: "")}$PART_SEPARATOR" +
                    encodeBase64(word.superlative ?: "")
            is Word.Adverb ->
                "ADVERB$PART_SEPARATOR${word.id}$PART_SEPARATOR" +
                    "${encodeBase64(word.text)}$PART_SEPARATOR$translations$PART_SEPARATOR" +
                    "$examples$PART_SEPARATOR${encodeBase64(word.comparative ?: "")}$PART_SEPARATOR" +
                    encodeBase64(word.superlative ?: "")
        }
    }

    private fun encodeBase64(value: String): String =
        Base64.encodeToString(value.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

    private fun decodeBase64(value: String): String = String(Base64.decode(value, Base64.NO_WRAP), Charsets.UTF_8)

    private fun deserializeWord(data: String): Word {
        val parts = data.split(PART_SEPARATOR)
        val type = parts[WORD_TYPE_INDEX]
        val id = parts[WORD_ID_INDEX]
        val text = decodeBase64(parts[WORD_TEXT_INDEX])
        val translations = if (parts[TRANSLATIONS_INDEX].isNotEmpty()) {
            parts[TRANSLATIONS_INDEX].split(ITEM_SEPARATOR).map {
                val translationParts = it.split(FIELD_SEPARATOR)
                Translation(
                    id = translationParts[WORD_TYPE_INDEX],
                    translation = decodeBase64(translationParts[WORD_ID_INDEX]),
                )
            }
        } else {
            emptyList()
        }
        val examples = if (parts[EXAMPLES_INDEX].isNotEmpty()) {
            parts[EXAMPLES_INDEX].split(ITEM_SEPARATOR).map {
                val exampleParts = it.split(FIELD_SEPARATOR)
                Example(
                    id = exampleParts[WORD_TYPE_INDEX],
                    example = decodeBase64(exampleParts[WORD_ID_INDEX]),
                )
            }
        } else {
            emptyList()
        }

        return when (type) {
            "NOUN" -> Word.Noun(
                id = id,
                text = text,
                translations = translations,
                examples = examples,
                gender = if (parts[EXTRA_FIELD_1_INDEX].isNotEmpty()) {
                    WordGender.valueOf(parts[EXTRA_FIELD_1_INDEX])
                } else {
                    null
                },
                pluralForm = decodeBase64(parts[EXTRA_FIELD_2_INDEX]).ifEmpty { null },
            )
            "VERB" -> Word.Verb(
                id = id,
                text = text,
                translations = translations,
                examples = examples,
                conjugation = decodeBase64(parts[EXTRA_FIELD_1_INDEX]).ifEmpty { null },
                management = decodeBase64(parts[EXTRA_FIELD_2_INDEX]).ifEmpty { null },
            )
            "ADJECTIVE" -> Word.Adjective(
                id = id,
                text = text,
                translations = translations,
                examples = examples,
                comparative = decodeBase64(parts[EXTRA_FIELD_1_INDEX]).ifEmpty { null },
                superlative = decodeBase64(parts[EXTRA_FIELD_2_INDEX]).ifEmpty { null },
            )
            "ADVERB" -> Word.Adverb(
                id = id,
                text = text,
                translations = translations,
                examples = examples,
                comparative = decodeBase64(parts[EXTRA_FIELD_1_INDEX]).ifEmpty { null },
                superlative = decodeBase64(parts[EXTRA_FIELD_2_INDEX]).ifEmpty { null },
            )
            else -> throw IllegalArgumentException("Unknown word type: $type")
        }
    }
}
