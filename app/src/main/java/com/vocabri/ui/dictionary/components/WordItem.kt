package com.vocabri.ui.dictionary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.dictionary.model.WordUi
import com.vocabri.ui.dictionary.viewmodel.DictionaryEvent
import com.vocabri.ui.theme.VocabriTheme

/**
 * Composable for a single word item in the list.
 */
@Composable
fun WordItem(
    modifier: Modifier = Modifier,
    uiItem: WordUi,
    onEvent: (DictionaryEvent) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        IconButton(
            onClick = { onEvent(DictionaryEvent.DeleteWordClicked(uiItem.id)) }
        ) {
            Icon(
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.dictionary_delete_word)
            )
        }
    }
}


@Preview(showBackground = true, name = "Single Word Item")
@Composable
fun PreviewWordItem() {
    val sampleWord = WordUi(
        id = "1",
        text = "lernen",
        translations = "learn, study",
        examples = listOf(),
        partOfSpeech = PartOfSpeech.VERB,
        notes = "Irregular verb"
    )
    VocabriTheme {
        WordItem(uiItem = sampleWord) {}
    }
}

