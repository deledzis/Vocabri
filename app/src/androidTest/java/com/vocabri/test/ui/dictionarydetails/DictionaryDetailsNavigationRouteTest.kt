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
package com.vocabri.test.ui.dictionarydetails

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.test.di.testModule
import com.vocabri.test.rule.KoinTestRule
import com.vocabri.ui.screens.dictionarydetails.DictionaryDetailsScreen
import com.vocabri.ui.screens.dictionarydetails.model.WordUiModel
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsContract
import com.vocabri.ui.theme.VocabriTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class DictionaryDetailsNavigationRouteTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val koinTestRule = KoinTestRule(
        listOf(
            testModule,
        ),
    )

    private val navController = mockk<NavHostController>(relaxed = true)

    @Test
    fun testInLoadingStateNoPatOfSpeechInTitleShown() {
        val viewModel = TestDictionaryDetailsViewModel(PartOfSpeech.NOUN)

        composeTestRule.setContent {
            VocabriTheme {
                DictionaryDetailsScreen(
                    viewModel = viewModel,
                    onNavigateToAddWord = { },
                    onNavigateToEditWord = { },
                )
            }
        }

        composeTestRule.onNodeWithText("Loading").assertDoesNotExist()
    }

    @Test
    fun testBackButtonInWordsLoadedState() {
        CountDownLatch(1)
        val viewModel = TestDictionaryDetailsViewModel(PartOfSpeech.VERB)

        viewModel.setState(
            DictionaryDetailsContract.UiState.WordsLoaded(
                titleId = R.string.verbs,
                words = listOf(
                    WordUiModel(
                        id = "1",
                        text = "lernen",
                        translations = "learn",
                        examples = "Ich lerne",
                        partOfSpeech = PartOfSpeech.VERB,
                    ),
                ),
            ),
        )

        composeTestRule.setContent {
            VocabriTheme {
                DictionaryDetailsScreen(
                    viewModel = viewModel,
                    onNavigateToAddWord = { },
                    onNavigateToEditWord = { navController.popBackStack() },
                )
            }
        }

        composeTestRule.onNodeWithText("lernen").assertExists().performClick()

        verify { navController.popBackStack() }
    }

    @Test
    fun testWordsDisplayedInWordsLoadedState() {
        val viewModel = TestDictionaryDetailsViewModel(PartOfSpeech.NOUN)

        viewModel.setState(
            DictionaryDetailsContract.UiState.WordsLoaded(
                titleId = R.string.verbs,
                listOf(
                    WordUiModel(
                        id = "2",
                        text = "Haus",
                        translations = "house",
                        examples = "Das ist ein Haus",
                        partOfSpeech = PartOfSpeech.NOUN,
                    ),
                ),
            ),
        )

        composeTestRule.setContent {
            VocabriTheme {
                DictionaryDetailsScreen(
                    viewModel = viewModel,
                    onNavigateToAddWord = { },
                    onNavigateToEditWord = { },
                )
            }
        }

        composeTestRule.onNodeWithText("Haus").assertExists()
    }
}
