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
package com.vocabri.ui.screens.dictionarydetails.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.components.DeleteBackground
import com.vocabri.ui.screens.dictionary.model.toSmallCircleColor
import com.vocabri.ui.screens.dictionarydetails.model.WordUiModel
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsEvent
import com.vocabri.ui.theme.VocabriTheme

private const val THRESHOLD = 0.5f

@Composable
fun WordListItem(modifier: Modifier = Modifier, uiItem: WordUiModel, onEvent: (DictionaryDetailsEvent) -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * THRESHOLD },
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onEvent(DictionaryDetailsEvent.DeleteWordClicked(uiItem.id))
                    true
                }

                else -> false
            }
        },
    )

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = { DeleteBackground() },
    ) {
        WordListItemContent(uiItem = uiItem, onEvent = onEvent)
    }
}

/**
 * Composable for a single word item in the list.
 */
@Composable
fun WordListItemContent(modifier: Modifier = Modifier, uiItem: WordUiModel, onEvent: (DictionaryDetailsEvent) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.Gray),
            ) { onEvent(DictionaryDetailsEvent.OnWordClicked(uiItem.id)) }
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp, end = 8.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(color = uiItem.partOfSpeech.toSmallCircleColor)
                .align(Alignment.Top),
        )
        // Display word text and its translations
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = uiItem.text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = uiItem.translations,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontStyle = FontStyle.Italic,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = stringResource(R.string.navigation_description_plus_button),
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Preview(
    name = "Single Word Item Night Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Single Word Item Day Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun PreviewWordListItem() {
    val sampleWord = WordUiModel(
        id = "1",
        text = "lernen",
        translations = "learn, study",
        examples = "Ich lerne Deutsch.",
        partOfSpeech = PartOfSpeech.VERB,
    )
    VocabriTheme {
        WordListItem(uiItem = sampleWord) {}
    }
}
