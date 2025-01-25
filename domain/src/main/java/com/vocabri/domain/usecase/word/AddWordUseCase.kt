package com.vocabri.domain.usecase.word

import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository
import com.vocabri.logger.logger

/**
 * Use case for adding a new word to the repository.
 *
 * This class encapsulates the logic for inserting a word into the data layer,
 * ensuring proper error handling and logging.
 *
 * @param wordRepository The repository responsible for word data operations.
 */
class AddWordUseCase(private val wordRepository: WordRepository) {

    private val log = logger()

    /**
     * Adds a word to the repository.
     *
     * If the insertion fails, the exception is logged and rethrown for higher-level handling.
     *
     * @param word The word to be added.
     * @throws Exception If an error occurs during the insertion process.
     */
    suspend fun execute(word: Word) {
        log.i { "Executing AddWordUseCase with word: $word" }

        try {
            wordRepository.insertWord(word)
            log.i { "Word successfully inserted: ${word.text}" }
        } catch (e: Exception) {
            log.e(e) { "Error inserting word: ${word.text}" }
            throw e
        }
    }
}
