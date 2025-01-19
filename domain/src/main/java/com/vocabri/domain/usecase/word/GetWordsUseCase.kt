package com.vocabri.domain.usecase.word

import com.vocabri.domain.repository.WordRepository

open class GetWordsUseCase(private val wordRepository: WordRepository) {
    open suspend fun execute() = wordRepository.getWords()
}
