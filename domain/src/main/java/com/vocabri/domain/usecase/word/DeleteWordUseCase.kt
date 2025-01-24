package com.vocabri.domain.usecase.word

import com.vocabri.domain.repository.WordRepository
import com.vocabri.domain.util.logger

/**
 * Use case for deleting a word from the repository.
 *
 * This class handles the logic for removing a word by its ID from the data layer.
 *
 * @param wordRepository The repository responsible for word data operations.
 */
class DeleteWordUseCase(private val wordRepository: WordRepository) {

    private val log = logger()

    /**
     * Deletes a word by its ID.
     *
     * @param id The unique identifier of the word to be deleted.
     * @throws Exception If an error occurs during the deletion process.
     */
    suspend fun execute(id: String) {
        log.i { "Executing DeleteWordUseCase for word ID: $id" }

        try {
            wordRepository.deleteWordById(id)
            log.i { "Word with ID $id successfully deleted" }
        } catch (e: Exception) {
            log.e(e) { "Error deleting word with ID: $id" }
            throw e
        }
    }
}
