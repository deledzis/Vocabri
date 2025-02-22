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
package com.vocabri.ui.screens.dictionary.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.components.Cards
import com.vocabri.ui.screens.dictionary.model.WordGroupUiModel
import com.vocabri.ui.screens.dictionary.viewmodel.DictionaryEvent
import com.vocabri.ui.theme.VocabriTheme

@Composable
fun WordGroupCard(modifier: Modifier = Modifier, uiItem: WordGroupUiModel, onEvent: (DictionaryEvent) -> Unit) {
    Cards.Solid(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = {
            onEvent(DictionaryEvent.OnGroupCardClicked(partOfSpeech = uiItem.partOfSpeech.toString()))
        },
    ) {
        WordGroupCardContent(
            uiItem = uiItem,
            titleColor = MaterialTheme.colorScheme.onSurface,
            subtitleColor = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun WordGroupHighlightedCard(
    modifier: Modifier = Modifier,
    uiItem: WordGroupUiModel,
    onEvent: (DictionaryEvent) -> Unit,
) {
    Cards.Solid(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = MaterialTheme.colorScheme.tertiary,
        onClick = {
            onEvent(DictionaryEvent.OnGroupCardClicked(partOfSpeech = uiItem.partOfSpeech.toString()))
        },
    ) {
        WordGroupCardContent(
            uiItem = uiItem,
            titleColor = MaterialTheme.colorScheme.onTertiary,
            subtitleColor = MaterialTheme.colorScheme.tertiaryContainer,
        )
    }
}

@Composable
private fun WordGroupCardContent(
    modifier: Modifier = Modifier,
    uiItem: WordGroupUiModel,
    titleColor: Color,
    subtitleColor: Color,
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = uiItem.titleText,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = titleColor,
        )
        Text(
            text = uiItem.subtitleText,
            style = MaterialTheme.typography.labelLarge,
            color = subtitleColor,
            fontStyle = FontStyle.Italic,
        )
    }
}

@Preview(
    name = "Night Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Day Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun WordGroupCardPreview() {
    VocabriTheme {
        WordGroupCard(
            uiItem = WordGroupUiModel(
                partOfSpeech = PartOfSpeech.NOUN,
                titleText = "Noun",
                subtitleText = "Words: 10",
            ),
        ) {}
    }
}

@Preview(
    name = "Night Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Day Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun WordWordGroupHighlightedCard() {
    VocabriTheme {
        WordGroupHighlightedCard(
            uiItem = WordGroupUiModel(
                partOfSpeech = PartOfSpeech.ALL,
                titleText = "All words",
                subtitleText = "Words: 10",
            ),
        ) {}
    }
}
