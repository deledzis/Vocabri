package com.vocabri.data

import com.vocabri.data.datasource.word.WordLocalDataSource
import com.vocabri.data.mapper.WordMapper
import com.vocabri.data.repository.word.WordRepositoryImpl
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WordRepositoryImplTest {

    private lateinit var repository: WordRepositoryImpl
    private val localDataSource: WordLocalDataSource = mockk(relaxed = true)

    @Before
    fun setup() {
        repository = WordRepositoryImpl(localDataSource, WordMapper())
    }

    @Test
    fun `saveWord saves word to localDataSource`() = runBlocking {
        val word = Word(
            id = "1",
            text = "lernen",
            translations = listOf("learn"),
            examples = emptyList(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = null
        )

        coEvery { localDataSource.addWord(any()) } returns Unit

        repository.saveWord(word)

        coVerify {
            localDataSource.addWord(
                WordEntity(
                    id = "1",
                    text = "lernen",
                    translations = "learn",
                    examples = "",
                    partOfSpeech = "VERB",
                    notes = null
                )
            )
        }
    }

    @Test
    fun `getWords retrieves words from localDataSource`() = runBlocking {
        val dbWords = listOf(
            WordEntity(
                id = "1",
                text = "lernen",
                translations = "learn",
                examples = "Ich lerne",
                partOfSpeech = "VERB",
                notes = null
            )
        )
        val expectedDomainWords = listOf(
            Word(
                id = "1",
                text = "lernen",
                translations = listOf("learn"),
                examples = listOf("Ich lerne"),
                partOfSpeech = PartOfSpeech.VERB,
                notes = null
            )
        )

        coEvery { localDataSource.getWords() } returns dbWords

        val result = repository.getWords()
        assertEquals(expectedDomainWords, result)
    }
}
