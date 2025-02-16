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
import com.vocabri.domain.model.word.WordGroup
import com.vocabri.domain.model.word.WordGroups
import com.vocabri.domain.model.word.toPartOfSpeech
import com.vocabri.domain.repository.WordRepository
import com.vocabri.logger.logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveWordGroupsUseCase(private val wordRepository: WordRepository) {

    private val log = logger()

    /**
     * Returns a reactive flow of [WordGroups], where each emission
     * represents up-to-date grouping by part of speech.
     */
    fun execute(): Flow<WordGroups> {
        log.i { "ObserveWordGroupsUseCase - creating flow of WordGroups" }

        return wordRepository.observeWordsByPartOfSpeech(partOfSpeech = PartOfSpeech.ALL)
            .map { words ->
                // Group them by the result of word.toPartOfSpeech()
                val groupedMap = words.groupBy { it.toPartOfSpeech() }

                // Transform the groups into a list of WordGroup
                val groups = groupedMap.map { (pos, groupedWords) ->
                    WordGroup(
                        partOfSpeech = pos,
                        wordCount = groupedWords.size,
                    )
                }

                log.i { "Observed ${words.size} total words, grouped into ${groups.size} categories" }

                WordGroups(
                    allWords = WordGroup(
                        partOfSpeech = PartOfSpeech.ALL,
                        wordCount = words.size,
                    ),
                    groups = groups,
                )
            }
    }
}
