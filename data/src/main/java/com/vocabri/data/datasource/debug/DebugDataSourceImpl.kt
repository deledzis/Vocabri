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
package com.vocabri.data.datasource.debug

import com.vocabri.domain.model.word.Example
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.WordGender
import com.vocabri.logger.logger
import com.vocabri.utils.IdGenerator
import kotlin.random.Random

@Suppress("MagicNumber")
class DebugDataSourceImpl(private val idGenerator: IdGenerator) : DebugDataSource {
    private val log = logger()

    /**
     * Generates a random Word instance of one of the sealed subtypes.
     */
    override fun generateRandomWord(): Word {
        log.i { "Generating a random word..." }

        // Randomly pick a part of speech
        return when (Random.nextInt(4)) {
            0 -> generateRandomNoun()
            1 -> generateRandomVerb()
            2 -> generateRandomAdjective()
            else -> generateRandomAdverb()
        }
    }

    /**
     * Generates a random Noun.
     */
    private fun generateRandomNoun(): Word.Noun {
        val noun = Word.Noun(
            id = generateRandomString(8),
            text = "Noun_${generateRandomString(4)}",
            translations = generateRandomTranslations(),
            examples = generateRandomExamples(),
            gender = randomGenderOrNull(),
            pluralForm = randomStringOrNull(prefix = "pl_"),
        )
        log.i { "Generated random noun: $noun" }
        return noun
    }

    /**
     * Generates a random Verb.
     */
    private fun generateRandomVerb(): Word.Verb {
        val verb = Word.Verb(
            id = generateRandomString(8),
            text = "Verb_${generateRandomString(4)}",
            translations = generateRandomTranslations(),
            examples = generateRandomExamples(),
            conjugation = randomStringOrNull(prefix = "conj_"),
            management = randomStringOrNull(prefix = "mgmt_"),
        )
        log.i { "Generated random verb: $verb" }
        return verb
    }

    /**
     * Generates a random Adjective.
     */
    private fun generateRandomAdjective(): Word.Adjective {
        val adjective = Word.Adjective(
            id = generateRandomString(8),
            text = "Adj_${generateRandomString(4)}",
            translations = generateRandomTranslations(),
            examples = generateRandomExamples(),
            comparative = randomStringOrNull(prefix = "comp_"),
            superlative = randomStringOrNull(prefix = "sup_"),
        )
        log.i { "Generated random adjective: $adjective" }
        return adjective
    }

    /**
     * Generates a random Adverb.
     */
    private fun generateRandomAdverb(): Word.Adverb {
        val adverb = Word.Adverb(
            id = generateRandomString(8),
            text = "Adv_${generateRandomString(4)}",
            translations = generateRandomTranslations(),
            examples = generateRandomExamples(),
            comparative = randomStringOrNull(prefix = "comp_"),
            superlative = randomStringOrNull(prefix = "sup_"),
        )
        log.i { "Generated random adverb: $adverb" }
        return adverb
    }

    /**
     * Returns a list of 1 to 3 random translations.
     */
    private fun generateRandomTranslations(): List<Translation> {
        val numberOfTranslations = Random.nextInt(1, 4)
        return (1..numberOfTranslations).map {
            Translation(
                id = idGenerator.generateStringId(),
                translation = "Tr_${generateRandomString(3)}",
            )
        }
    }

    /**
     * Returns a list of 0 to 2 random examples.
     */
    private fun generateRandomExamples(): List<Example> {
        val numberOfExamples = Random.nextInt(0, 3)
        return (1..numberOfExamples).map {
            Example(
                id = idGenerator.generateStringId(),
                example = "Ex_${generateRandomString(5)}",
            )
        }
    }

    /**
     * Generates a random string of [length] letters.
     * Default length is 6.
     */
    private fun generateRandomString(length: Int = 6): String {
        val chars = ('a'..'z')
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    /**
     * Generates either a random string with the given prefix or returns null.
     */
    private fun randomStringOrNull(prefix: String = ""): String? = if (Random.nextBoolean()) {
        "$prefix${generateRandomString()}"
    } else {
        null
    }

    /**
     * Returns a random gender or null.
     */
    private fun randomGenderOrNull(): WordGender? {
        // 3 genders + 1 possibility of null
        return when (Random.nextInt(4)) {
            0 -> WordGender.MASCULINE
            1 -> WordGender.FEMININE
            2 -> WordGender.NEUTER
            else -> null
        }
    }
}
