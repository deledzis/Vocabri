package com.vocabri.fake

import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository
import com.vocabri.domain.usecase.word.GetWordsUseCase

class FakeGetWordsUseCase(wordRepository: WordRepository) : GetWordsUseCase(wordRepository) {
    override suspend fun execute(): List<Word> {
        return listOf(
            Word(
                id = "1",
                text = "lernen",
                translations = listOf("learn"),
                examples = emptyList(),
                partOfSpeech = PartOfSpeech.VERB,
                notes = null
            ),
            Word(
                id = "2",
                text = "Haus",
                translations = listOf("house"),
                examples = emptyList(),
                partOfSpeech = PartOfSpeech.NOUN,
                notes = null
            )
        )
    }
}
