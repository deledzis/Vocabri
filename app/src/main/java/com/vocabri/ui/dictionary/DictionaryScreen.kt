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
package com.vocabri.ui.dictionary

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.logger.logger
import com.vocabri.ui.components.IconButtonWithCenteredText
import com.vocabri.ui.components.ShimmerEffect
import com.vocabri.ui.dictionary.components.WordGroupCard
import com.vocabri.ui.dictionary.model.WordGroupUiModel
import com.vocabri.ui.dictionary.viewmodel.DictionaryEvent
import com.vocabri.ui.dictionary.viewmodel.DictionaryState
import com.vocabri.ui.dictionary.viewmodel.DictionaryViewModel
import com.vocabri.ui.theme.VocabriTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun DictionaryScreen(navController: NavController, viewModel: DictionaryViewModel = koinViewModel()) {
    val log = logger("DictionaryScreen")
    val state by viewModel.state.collectAsState()

    log.i { "DictionaryScreen is displayed" }

    LaunchedEffect(viewModel) {
        log.i { "Triggering initial load of words" }
        viewModel.handleEvent(DictionaryEvent.LoadWords)
    }

    DictionaryScreenRoot(state) { event ->
        log.i { "Handling event: $event" }
        when (event) {
            is DictionaryEvent.AddWordClicked -> navController.navigate("addWord")
            is DictionaryEvent.LoadWords -> viewModel.handleEvent(event)
            is DictionaryEvent.OnGroupCardClicked -> navController.navigate("groupDetails/${event.wordGroup}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreenRoot(state: DictionaryState, onEvent: (DictionaryEvent) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dictionary_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                },
                actions = {
                    if (state is DictionaryState.GroupsLoaded) {
                        IconButton(onClick = { onEvent(DictionaryEvent.AddWordClicked) }) {
                            Icon(
                                tint = MaterialTheme.colorScheme.tertiary,
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.add_word),
                            )
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues),
        ) {
            when (state) {
                is DictionaryState.GroupsLoaded -> {
                    WordListScreen(state = state, onEvent = onEvent)
                }

                is DictionaryState.Empty -> {
                    EmptyScreen(onEvent = onEvent)
                }

                is DictionaryState.Loading -> {
                    LoadingScreen()
                }

                is DictionaryState.Error -> {
                    ErrorScreen(state = state)
                }
            }
        }
    }
}

/**
 * Displays a message when the dictionary is empty.
 */
@Composable
internal fun EmptyScreen(modifier: Modifier = Modifier, onEvent: (DictionaryEvent) -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier
                    .padding(32.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.dictionary_empty_message),
                style = MaterialTheme.typography.displaySmall,
            )
            IconButtonWithCenteredText(
                modifier = Modifier
                    .fillMaxWidth(0.7f),
                textId = R.string.add_word,
                contentDescriptionId = R.string.add_word,
                icon = Icons.Default.Create,
                onClick = {
                    onEvent(DictionaryEvent.AddWordClicked)
                },
            )
        }
    }
}

/**
 * Displays a loading indicator while words are being loaded.
 */
@Composable
internal fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
    ) {
        repeat(10) {
            ShimmerEffect(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(4.dp),
            )
        }
    }
}

/**
 * Displays a list of word groups.
 */
@Composable
internal fun WordListScreen(
    modifier: Modifier = Modifier,
    state: DictionaryState.GroupsLoaded,
    onEvent: (DictionaryEvent) -> Unit,
) {
    val groups = state.groups
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(groups) { uiItem ->
            WordGroupCard(uiItem = uiItem, onEvent = onEvent)
        }
    }
}

/**
 * Displays an error message.
 */
@Composable
internal fun ErrorScreen(modifier: Modifier = Modifier, state: DictionaryState.Error) {
    Text(
        modifier = modifier
            .padding(16.dp),
        text = stringResource(R.string.error_message, state.message),
        style = MaterialTheme.typography.labelLarge,
    )
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
fun PreviewEmptyDictionaryScreen() {
    VocabriTheme {
        DictionaryScreenRoot(
            state = DictionaryState.Empty,
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
fun PreviewLoadingScreen() {
    VocabriTheme {
        DictionaryScreenRoot(state = DictionaryState.Loading) {}
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
fun PreviewErrorScreen() {
    VocabriTheme {
        DictionaryScreenRoot(state = DictionaryState.Error(message = "Network error")) {}
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
fun PreviewWordListScreen() {
    val sampleWordGroups = listOf(
        WordGroupUiModel(
            partOfSpeech = PartOfSpeech.VERB,
            titleText = "Verbs",
            subtitleText = "Words: 4",
        ),
        WordGroupUiModel(
            partOfSpeech = PartOfSpeech.NOUN,
            titleText = "Nouns",
            subtitleText = "Words: 14",
        ),
    )
    VocabriTheme {
        DictionaryScreenRoot(state = DictionaryState.GroupsLoaded(groups = sampleWordGroups)) {}
    }
}
