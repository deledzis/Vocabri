/*
 * MIT License
 *
 * Copyright (c) 2025 Aleksandr Stiagov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.vocabri.ui.dictionarydetails

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.fake.TestApplication
import com.vocabri.ui.screens.dictionarydetails.components.WordListItem
import com.vocabri.ui.screens.dictionarydetails.model.WordUiModel
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class WordItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `WordItem displays word text`() {
        val word = WordUiModel(
            id = "1",
            text = "lernen",
            translations = "learn",
            examples = "Ich lerne",
            partOfSpeech = PartOfSpeech.VERB,
        )

        composeRule.setContent {
            WordListItem(uiItem = word, onDeleteWord = {}, onWordClick = {})
        }

        composeRule.onNodeWithText("lernen").assertExists()
    }

    @Test
    fun `WordItem displays translations`() {
        val word = WordUiModel(
            id = "1",
            text = "lernen",
            translations = "learn, study",
            examples = "Ich lerne",
            partOfSpeech = PartOfSpeech.VERB,
        )

        composeRule.setContent {
            WordListItem(uiItem = word, onDeleteWord = {}, onWordClick = {})
        }

        composeRule.onNodeWithText("learn, study").assertExists()
    }

    @Test
    fun `Swiping end to start triggers delete event`() {
        val word = WordUiModel(
            id = "1",
            text = "lernen",
            translations = "learn",
            examples = "Ich lerne",
            partOfSpeech = PartOfSpeech.VERB,
        )
        val events = mutableListOf<DictionaryDetailsContract.UiEvent>()

        composeRule.setContent {
            WordListItem(
                uiItem = word,
                onWordClick = {},
                onDeleteWord = { events.add(DictionaryDetailsContract.UiEvent.OnDeleteWordClicked(word.id)) },
            )
        }

        composeRule.onNodeWithText("lernen")
            .performTouchInput { swipeLeft() }

        composeRule.waitForIdle()

        assertEquals(
            listOf(DictionaryDetailsContract.UiEvent.OnDeleteWordClicked(word.id)),
            events,
        )
    }

    @Test
    fun `Swiping start to end does not trigger delete event`() {
        val word = WordUiModel(
            id = "1",
            text = "lernen",
            translations = "learn",
            examples = "Ich lerne",
            partOfSpeech = PartOfSpeech.VERB,
        )
        val events = mutableListOf<DictionaryDetailsContract.UiEvent>()

        composeRule.setContent {
            WordListItem(
                uiItem = word,
                onWordClick = {},
                onDeleteWord = { events.add(DictionaryDetailsContract.UiEvent.OnDeleteWordClicked(word.id)) },
            )
        }

        composeRule.onNodeWithText("lernen")
            .performTouchInput { swipeRight() }

        composeRule.waitForIdle()

        assertTrue(events.isEmpty())
    }
}
