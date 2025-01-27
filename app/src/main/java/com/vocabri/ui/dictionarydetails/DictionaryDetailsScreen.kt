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
package com.vocabri.ui.dictionarydetails

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.vocabri.ui.dictionarydetails.components.WordListItem
import com.vocabri.ui.dictionarydetails.model.WordUiModel
import com.vocabri.ui.dictionarydetails.viewmodel.DictionaryDetailsEvent
import com.vocabri.ui.dictionarydetails.viewmodel.DictionaryDetailsState
import com.vocabri.ui.dictionarydetails.viewmodel.DictionaryDetailsViewModel
import com.vocabri.ui.theme.VocabriTheme
import org.koin.androidx.compose.koinViewModel

/**
 * DictionaryScreen displays the list of words in the user's dictionary
 * and provides actions for managing the dictionary.
 */
@Composable
fun DictionaryDetailsScreen(
    viewModel: DictionaryDetailsViewModel = koinViewModel(),
    navController: NavController,
    wordGroup: String,
) {
    val log = logger("DictionaryDetailsScreen")
    val state by viewModel.state.collectAsState()

    log.i { "DictionaryDetailsScreen is displayed" }

    LaunchedEffect(viewModel) {
        log.i { "Triggering initial load of words" }
        viewModel.handleEvent(DictionaryDetailsEvent.LoadWords(wordGroup))
    }

    DictionaryDetailsScreenRoot(state) { event ->
        log.i { "Handling event: $event" }
        when (event) {
            is DictionaryDetailsEvent.AddWordClicked -> navController.navigate("addWord")
            is DictionaryDetailsEvent.LoadWords -> viewModel.handleEvent(event)
            is DictionaryDetailsEvent.DeleteWordClicked -> viewModel.handleEvent(event)
            DictionaryDetailsEvent.OnBackClicked -> navController.popBackStack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryDetailsScreenRoot(state: DictionaryDetailsState, onEvent: (DictionaryDetailsEvent) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(state.titleId),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(DictionaryDetailsEvent.OnBackClicked) }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                        )
                    }
                },
                actions = {
                    if (state is DictionaryDetailsState.WordsLoaded) {
                        IconButton(onClick = { onEvent(DictionaryDetailsEvent.AddWordClicked) }) {
                            Icon(
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(24.dp),
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
                is DictionaryDetailsState.Empty -> {
                    EmptyScreen(onEvent = onEvent)
                }

                is DictionaryDetailsState.Loading -> {
                    LoadingScreen()
                }

                is DictionaryDetailsState.WordsLoaded -> {
                    WordListScreen(
                        state = state,
                        onEvent = onEvent,
                    )
                }

                is DictionaryDetailsState.Error -> {
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
fun EmptyScreen(modifier: Modifier = Modifier, onEvent: (DictionaryDetailsEvent) -> Unit) {
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
                    onEvent(DictionaryDetailsEvent.AddWordClicked)
                },
            )
        }
    }
}

/**
 * Displays a loading indicator while words are being loaded.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
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
 * Displays a list of words.
 */
@Composable
fun WordListScreen(
    modifier: Modifier = Modifier,
    state: DictionaryDetailsState.WordsLoaded,
    onEvent: (DictionaryDetailsEvent) -> Unit,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(state.words.size) { index ->
            WordListItem(uiItem = state.words[index], onEvent = onEvent)
        }
    }
}

/**
 * Displays an error message.
 */
@Composable
fun ErrorScreen(modifier: Modifier = Modifier, state: DictionaryDetailsState.Error) {
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
        DictionaryDetailsScreenRoot(
            state = DictionaryDetailsState.Empty(titleId = R.string.adverbs),
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
        DictionaryDetailsScreenRoot(state = DictionaryDetailsState.Loading(titleId = R.string.nouns)) {}
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
        DictionaryDetailsScreenRoot(
            state = DictionaryDetailsState.Error(
                titleId = R.string.verbs,
                message = "Network error",
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
fun PreviewWordListScreen() {
    val sampleWords = listOf(
        WordUiModel(
            id = "1",
            text = "lernen",
            translations = "learn",
            examples = "Ich lerne Deutsch.",
            partOfSpeech = PartOfSpeech.VERB.toString(),
            notes = null,
        ),
        WordUiModel(
            id = "2",
            text = "Haus",
            translations = "house, home",
            examples = "Das ist mein Haus.",
            partOfSpeech = PartOfSpeech.NOUN.toString(),
            notes = null,
        ),
    )
    VocabriTheme {
        DictionaryDetailsScreenRoot(
            state = DictionaryDetailsState.WordsLoaded(
                titleId = R.string.adverbs,
                words = sampleWords,
            ),
        ) {}
    }
}
