package com.vocabri.ui.dictionary

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.dictionary.model.WordUi
import com.vocabri.ui.dictionary.viewmodel.DictionaryState
import com.vocabri.ui.theme.VocabriTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DictionaryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val navController = mockk<NavHostController>(relaxed = true)

    @Test
    fun testWordsLoadSuccessfully() {
        composeTestRule.setContent {
            VocabriTheme {
                DictionaryScreen(
                    navController = navController
                )
            }
        }

        composeTestRule.onNodeWithText("Dictionary").assertExists() // Toolbar title
        composeTestRule.onNodeWithText("The dictionary is empty. Add your first word!")
            .assertExists()
    }

    @Test
    fun testAddWordButtonInIdleState() {
        composeTestRule.setContent {
            VocabriTheme {
                DictionaryScreen(
                    navController = navController
                )
            }
        }

        composeTestRule.onNodeWithText("Add Word").assertExists().performClick()

        verify { navController.navigate("addWord") }
    }

    @Test
    fun testAddWordButtonInWordsLoadedState() {
        val viewModel = TestDictionaryViewModel()

        // Set state to WordsLoaded
        viewModel.setState(
            DictionaryState.WordsLoaded(
                listOf(
                    WordUi(
                        id = "1",
                        text = "lernen",
                        translations = "learn",
                        examples = emptyList(),
                        partOfSpeech = PartOfSpeech.VERB,
                        notes = null
                    )
                )
            )
        )

        composeTestRule.setContent {
            VocabriTheme {
                DictionaryScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }

        // Check that the "Add" button exists and is clickable
        composeTestRule.onNodeWithContentDescription("Add Word").assertExists().performClick()

        verify { navController.navigate("addWord") }
    }
}
