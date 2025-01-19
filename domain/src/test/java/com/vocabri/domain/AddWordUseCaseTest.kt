package com.vocabri.domain

import com.vocabri.domain.fake.FakeWordRepository
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.usecase.word.AddWordUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddWordUseCaseTest {

    private lateinit var fakeRepository: FakeWordRepository
    private lateinit var addWordUseCase: AddWordUseCase

    @Before
    fun setup() {
        fakeRepository = FakeWordRepository()
        addWordUseCase = AddWordUseCase(fakeRepository)
    }

    @Test
    fun `execute adds word to repository`() = runTest {
        val word = Word(
            id = "1",
            text = "lernen",
            translations = listOf("learn"),
            examples = listOf(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = "Irregular verb"
        )

        addWordUseCase.execute(word)

        val words = fakeRepository.getWords()
        assertEquals(1, words.size)
        assertEquals("lernen", words[0].text)
        assertEquals(PartOfSpeech.VERB, words[0].partOfSpeech)
    }

    @Test
    fun `getWords retrieves all words from repository`() = runTest {
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

        val words = fakeRepository.getWords()
        assertEquals(2, words.size)
        assertEquals("Haus", words[1].text)
        assertEquals(PartOfSpeech.NOUN, words[1].partOfSpeech)
    }
}
