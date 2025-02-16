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
package com.vocabri.domain.usecase.word

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.model.word.WordGroup
import com.vocabri.domain.model.word.WordGroups
import com.vocabri.domain.model.word.toPartOfSpeech
import com.vocabri.domain.repository.WordRepository
import com.vocabri.logger.logger

/**
 * Use case for fetching word groups based on part of speech.
 *
 * This class retrieves all words from the repository, groups them by their part-of-speech type,
 * and constructs a WordGroups object containing the total count and grouped counts.
 */
class GetWordGroupsUseCase(private val wordRepository: WordRepository) {

    private val log = logger()

    /**
     * Fetches groups of words and their counts grouped by part of speech.
     *
     * @return [WordGroups] containing the total words and a list of [WordGroup].
     * @throws Exception If an error occurs during the fetch process.
     */
    suspend fun execute(): WordGroups {
        log.i { "Executing GetWordGroupsUseCase" }

        // 1. Get all words from the repository
        val words: List<Word> = wordRepository.getWordsByPartOfSpeech(partOfSpeech = PartOfSpeech.ALL)

        // 2. Group them by the result of word.toPartOfSpeech()
        val groupedMap = words.groupBy { it.toPartOfSpeech() }

        // 3. Transform the groups into a list of WordGroup
        val groups = groupedMap.map { (pos, groupedWords) ->
            WordGroup(
                partOfSpeech = pos,
                wordCount = groupedWords.size,
            )
        }

        log.i { "Successfully fetched ${groups.size} word groups" }

        // 4. Construct the WordGroups object
        //    - 'allWords' has partOfSpeech = PartOfSpeech.ALL (if you use it in your enum),
        //      and the total count of words.
        return WordGroups(
            allWords = WordGroup(
                partOfSpeech = PartOfSpeech.ALL,
                wordCount = words.size,
            ),
            groups = groups,
        )
    }
}
