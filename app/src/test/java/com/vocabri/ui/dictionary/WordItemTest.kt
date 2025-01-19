package com.vocabri.ui.dictionary

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vocabri.di.testModule
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.fake.TestApplication
import com.vocabri.rules.KoinTestRule
import com.vocabri.ui.dictionary.components.WordItem
import com.vocabri.ui.dictionary.model.WordUi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class WordItemTest {

    @get:Rule
    val koinTestRule = KoinTestRule(modules = listOf(testModule))

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `WordItem displays word text`() {
        val word = WordUi(
            id = "1",
            text = "lernen",
            translations = "learn",
            examples = emptyList(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = null
        )

        composeRule.setContent {
            WordItem(uiItem = word) {}
        }

        composeRule.onNodeWithText("lernen").assertExists()
    }

    @Test
    fun `WordItem displays translations`() {
        val word = WordUi(
            id = "1",
            text = "lernen",
            translations = "learn, study",
            examples = emptyList(),
            partOfSpeech = PartOfSpeech.VERB,
            notes = null
        )

        composeRule.setContent {
            WordItem(uiItem = word) {}
        }

        composeRule.onNodeWithText("learn, study").assertExists()
    }
}
