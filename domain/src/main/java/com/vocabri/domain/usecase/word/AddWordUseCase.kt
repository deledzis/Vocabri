package com.vocabri.domain.usecase.word

import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository

class AddWordUseCase(private val wordRepository: WordRepository) {
    suspend fun execute(word: Word) {
        wordRepository.saveWord(word)
    }
}
