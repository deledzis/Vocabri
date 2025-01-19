package com.vocabri.domain.usecase.word

import com.vocabri.domain.repository.WordRepository

/**
 * Use case for deleting a word from the repository.
 * This ensures the business logic for word deletion is encapsulated and reusable.
 *
 * @property wordRepository The repository handling word data.
 */
class DeleteWordUseCase(
    private val wordRepository: WordRepository
) {
    /**
     * Executes the use case to delete a word by its ID.
     *
     * @param id The unique ID of the word to delete.
     */
    suspend fun execute(id: String) {
        wordRepository.deleteWord(id)
    }
}
