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
package com.vocabri.ui.dictionary

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.fake.TestApplication
import com.vocabri.ui.dictionary.components.WordListItem
import com.vocabri.ui.dictionary.model.WordUiModel
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
            partOfSpeech = PartOfSpeech.VERB.toString(),
            notes = null,
        )

        composeRule.setContent {
            WordListItem(uiItem = word) {}
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
            partOfSpeech = PartOfSpeech.VERB.toString(),
            notes = null,
        )

        composeRule.setContent {
            WordListItem(uiItem = word) {}
        }

        composeRule.onNodeWithText("learn, study").assertExists()
    }
}
