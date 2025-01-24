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
