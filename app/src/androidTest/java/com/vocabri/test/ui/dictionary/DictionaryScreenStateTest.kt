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
package com.vocabri.test.ui.dictionary

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vocabri.R
import com.vocabri.ui.screens.dictionary.DictionaryScreenRoot
import com.vocabri.ui.screens.dictionary.viewmodel.DictionaryEvent
import com.vocabri.ui.screens.dictionary.viewmodel.DictionaryState
import com.vocabri.ui.theme.VocabriTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DictionaryScreenStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsAddWordCta() {
        composeTestRule.setContent {
            VocabriTheme {
                DictionaryScreenRoot(state = DictionaryState.Empty, onEvent = {})
            }
        }

        composeTestRule.onNodeWithText("Add Word").assertIsDisplayed()
        composeTestRule.onNodeWithText("The dictionary is empty.\nAdd your first word!").assertIsDisplayed()
    }

    @Test
    fun errorState_showsRetry_andDispatchesEvent() {
        val events = mutableListOf<DictionaryEvent>()

        composeTestRule.setContent {
            VocabriTheme {
                DictionaryScreenRoot(
                    state = DictionaryState.Error(message = "Network error"),
                    onEvent = { events.add(it) },
                )
            }
        }

        composeTestRule.onNodeWithText("An error occurred: Network error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed().performClick()

        assert(events.contains(DictionaryEvent.RetryClicked))
    }
}
