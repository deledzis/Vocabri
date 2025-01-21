package com.vocabri.test.ui.dictionary

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vocabri.data.di.dataModule
import com.vocabri.di.appModule
import com.vocabri.domain.di.domainModule
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.test.di.testModule
import com.vocabri.test.rule.KoinTestRule
import com.vocabri.ui.dictionary.DictionaryScreen
import com.vocabri.ui.dictionary.model.WordUiModel
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

    @get:Rule
    val koinTestRule = KoinTestRule(
        listOf(
            domainModule,
            dataModule,
            appModule,
            testModule
        )
    )

    private val navController = mockk<NavHostController>(relaxed = true)

    @Test
    fun testWordsLoadSuccessfully() {
        val viewModel = TestDictionaryViewModel()

        composeTestRule.setContent {
            VocabriTheme {
                DictionaryScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }

        composeTestRule.onNodeWithText("Dictionary").assertExists()
        composeTestRule.onNodeWithText("The dictionary is empty.\nAdd your first word!")
            .assertExists()
    }

    @Test
    fun testAddWordButtonInIdleState() {
        val viewModel = TestDictionaryViewModel()

        composeTestRule.setContent {
            VocabriTheme {
                DictionaryScreen(
                    viewModel = viewModel,
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
                    WordUiModel(
                        id = "1",
                        text = "lernen",
                        translations = "learn",
                        examples = "Ich lerne",
                        partOfSpeech = PartOfSpeech.VERB.toString(),
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
