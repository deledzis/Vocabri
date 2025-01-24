package com.vocabri.domain

import com.vocabri.domain.fake.FakeWordRepository
import com.vocabri.domain.model.word.Example
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.repository.WordRepository
import com.vocabri.domain.usecase.word.AddWordUseCase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class AddWordUseCaseTest {

    private lateinit var fakeRepository: WordRepository
    private lateinit var addWordUseCase: AddWordUseCase

    @Before
    fun setup() {
        fakeRepository = FakeWordRepository()
        addWordUseCase = AddWordUseCase(fakeRepository)
    }

    @Test
    fun `execute adds word with details to repository`() = runTest {
        val word = Word(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("1", "learn")),
            examples = listOf(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = "Irregular verb",
        )

        addWordUseCase.execute(word)

        val words = fakeRepository.getAllWords()
        assertEquals(1, words.size)

        val addedWord = words[0]
        assertEquals("lernen", addedWord.text)
        assertEquals(PartOfSpeech.VERB, addedWord.partOfSpeech)
        assertEquals("Irregular verb", addedWord.notes)
        assertEquals(1, addedWord.translations.size)
        assertEquals("learn", addedWord.translations[0].text)
    }

    @Test
    fun `getWords retrieves all words with details from repository`() = runTest {
        val word1 = Word(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("1", "learn")),
            examples = listOf(Example("1", "Ich lerne")),
            partOfSpeech = PartOfSpeech.VERB,
            notes = null,
        )
        val word2 = Word(
            id = "2",
            text = "Haus",
            translations = listOf(Translation("2", "house")),
            examples = listOf(Example("2", "Das ist mein Haus")),
            partOfSpeech = PartOfSpeech.NOUN,
            notes = null,
        )

        fakeRepository.insertWord(word1)
        fakeRepository.insertWord(word2)

        val words = fakeRepository.getAllWords()
        assertEquals(2, words.size)

        val retrievedWord1 = words[0]
        assertEquals("lernen", retrievedWord1.text)
        assertEquals(PartOfSpeech.VERB, retrievedWord1.partOfSpeech)
        assertEquals(1, retrievedWord1.translations.size)
        assertEquals("learn", retrievedWord1.translations[0].text)
        assertEquals(1, retrievedWord1.examples.size)
        assertEquals("Ich lerne", retrievedWord1.examples[0].text)

        val retrievedWord2 = words[1]
        assertEquals("Haus", retrievedWord2.text)
        assertEquals(PartOfSpeech.NOUN, retrievedWord2.partOfSpeech)
        assertEquals(1, retrievedWord2.translations.size)
        assertEquals("house", retrievedWord2.translations[0].text)
        assertEquals(1, retrievedWord2.examples.size)
        assertEquals("Das ist mein Haus", retrievedWord2.examples[0].text)
    }

    @Test
    fun `execute does not add duplicate words to repository`() = runTest {
        val word = Word(
            id = "1",
            text = "lernen",
            translations = listOf(Translation("1", "learn")),
            examples = listOf(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = "Irregular verb",
        )

        addWordUseCase.execute(word)

        assertThrows(
            "Word with text 'lernen' already exists",
            IllegalArgumentException::class.java,
        ) {
            runBlocking { addWordUseCase.execute(word) }
        }

        val words = fakeRepository.getAllWords()
        assertEquals(1, words.size)
    }
}
