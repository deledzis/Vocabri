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
package com.vocabri.ui.dictionarydetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.components.DeleteBackground
import com.vocabri.ui.dictionarydetails.model.WordUiModel
import com.vocabri.ui.dictionarydetails.viewmodel.DictionaryDetailsEvent
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

                SwipeToDismissBoxValue.StartToEnd -> return@rememberSwipeToDismissBoxState false
                SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = { DeleteBackground() },
    ) {
        WordListItemContent(uiItem = uiItem)
    }
}

/**
 * Composable for a single word item in the list.
 */
@Composable
fun WordListItemContent(modifier: Modifier = Modifier, uiItem: WordUiModel) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Display word text and its translations
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = uiItem.text,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = uiItem.translations,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        // Delete button for manual deletion
        /*IconButton(
            onClick = { onEvent(DictionaryEvent.DeleteWordClicked(uiItem.id)) }
        ) {
            Icon(
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.dictionary_delete_word)
            )
        }*/
    }
}

@Preview(showBackground = true, name = "Single Word Item")
@Composable
fun PreviewWordListItem() {
    val sampleWord = WordUiModel(
        id = "1",
        text = "lernen",
        translations = "learn, study",
        examples = "Ich lerne Deutsch.",
        partOfSpeech = PartOfSpeech.VERB.toString(),
        notes = "Irregular verb",
    )
    VocabriTheme {
        WordListItem(uiItem = sampleWord) {}
    }
}
