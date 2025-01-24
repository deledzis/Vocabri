package com.vocabri.domain

import com.vocabri.domain.fake.FakeWordRepository
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.usecase.word.DeleteWordUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeleteWordUseCaseTest {

    private lateinit var useCase: DeleteWordUseCase
    private lateinit var fakeRepository: FakeWordRepository

    @Before
    fun setup() {
        fakeRepository = FakeWordRepository()
        useCase = DeleteWordUseCase(fakeRepository)
    }

    @Test
    fun `deleteWord removes the word from repository`() = runBlocking {
        val word1 = Word(
            id = "1",
            text = "lernen",
            translations = emptyList(),
            examples = emptyList(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = null,
        )
        val word2 = Word(
            id = "2",
            text = "Haus",
            translations = emptyList(),
            examples = emptyList(),
            partOfSpeech = PartOfSpeech.NOUN,
            notes = null,
        )
        fakeRepository.insertWord(word1)
        fakeRepository.insertWord(word2)

        // Act
        useCase.execute(word1.id)

        // Assert
        val remainingWords = fakeRepository.getAllWords()
        assertEquals(1, remainingWords.size)
        assertEquals(word2, remainingWords.first())
    }

    @Test
    fun `deleteWord does nothing if word does not exist`() = runBlocking {
        val word = Word(
            id = "1",
            text = "lernen",
            translations = emptyList(),
            examples = emptyList(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = null,
        )
        fakeRepository.insertWord(word)

        // Act
        useCase.execute("non-existent-id")

        // Assert
        val remainingWords = fakeRepository.getAllWords()
        assertEquals(1, remainingWords.size)
        assertEquals(word, remainingWords.first())
    }

    @Test
    fun `deleteWord allows deleting multiple words`() = runBlocking {
        val word1 = Word(
            id = "1",
            text = "lernen",
            translations = emptyList(),
            examples = emptyList(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = null,
        )
        val word2 = Word(
            id = "2",
            text = "Haus",
            translations = emptyList(),
            examples = emptyList(),
            partOfSpeech = PartOfSpeech.NOUN,
            notes = null,
        )
        fakeRepository.insertWord(word1)
        fakeRepository.insertWord(word2)

        // Act
        useCase.execute(word1.id)
        useCase.execute(word2.id)

        // Assert
        val remainingWords = fakeRepository.getAllWords()
        assertEquals(0, remainingWords.size)
    }
}
