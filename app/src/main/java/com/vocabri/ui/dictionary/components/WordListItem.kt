package com.vocabri.ui.dictionary.components

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
import com.vocabri.ui.dictionary.model.WordUiModel
import com.vocabri.ui.dictionary.viewmodel.DictionaryEvent
import com.vocabri.ui.theme.VocabriTheme

const val Threshold = 0.5f

@Composable
fun WordListItem(
    modifier: Modifier = Modifier,
    uiItem: WordUiModel,
    onEvent: (DictionaryEvent) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * Threshold },
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onEvent(DictionaryEvent.DeleteWordClicked(uiItem.id))
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
fun WordListItemContent(
    modifier: Modifier = Modifier,
    uiItem: WordUiModel,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display word text and its translations
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = uiItem.text,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = uiItem.translations,
                style = MaterialTheme.typography.labelSmall
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
        notes = "Irregular verb"
    )
    VocabriTheme {
        WordListItem(uiItem = sampleWord) {}
    }
}

