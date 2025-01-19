package com.vocabri.domain

import com.vocabri.domain.fake.FakeWordRepository
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.usecase.word.GetWordsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWordsUseCaseTest {

    private lateinit var fakeRepository: FakeWordRepository
    private lateinit var getWordsUseCase: GetWordsUseCase

    @Before
    fun setup() {
        fakeRepository = FakeWordRepository()
        getWordsUseCase = GetWordsUseCase(fakeRepository)
    }

    @Test
    fun `execute retrieves words from repository`() = runTest {
        val word1 = Word(
            id = "1",
            text = "lernen",
            translations = listOf("learn"),
            examples = listOf(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = null
        )
        val word2 = Word(
            id = "2",
            text = "Haus",
            translations = listOf("house"),
            examples = listOf(),
            partOfSpeech = PartOfSpeech.NOUN,
            notes = null
        )

        fakeRepository.saveWord(word1)
        fakeRepository.saveWord(word2)

        val words = getWordsUseCase.execute()
        assertEquals(2, words.size)
        assertEquals("lernen", words[0].text)
        assertEquals("Haus", words[1].text)
    }
}
