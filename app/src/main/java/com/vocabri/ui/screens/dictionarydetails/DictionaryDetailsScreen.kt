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
package com.vocabri.ui.screens.dictionarydetails

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.logger.logger
import com.vocabri.ui.components.ShimmerEffect
import com.vocabri.ui.navigation.NavigationRoute
import com.vocabri.ui.screens.dictionarydetails.components.WordListItem
import com.vocabri.ui.screens.dictionarydetails.model.WordUiModel
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsEvent
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsState
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsViewModel
import com.vocabri.ui.theme.VocabriTheme

private const val LOADING_SKELETONS_COUNT = 8
private const val LOADING_SKELETON_TITLE_WIDTH_PERCENT = 0.5f
private const val LOADING_SKELETON_SUBTITLE_WIDTH_PERCENT = 0.7f

/**
 * DictionaryScreen displays the list of words in the user's dictionary
 * and provides actions for managing the dictionary.
 */
@Composable
fun DictionaryDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: DictionaryDetailsViewModel,
    navController: NavController,
) {
    val log = logger("DictionaryDetailsScreen")
    val state by viewModel.state.collectAsState()

    log.i { "DictionaryDetailsScreen is displayed" }

    LaunchedEffect(viewModel) {
        log.i { "Triggering initial load of words" }
        viewModel.handleEvent(DictionaryDetailsEvent.LoadWords)
    }

    DictionaryDetailsScreenRoot(modifier = modifier, state = state) { event ->
        log.i { "Handling event: $event" }
        when (event) {
            is DictionaryDetailsEvent.AddWordClicked -> {
                navController.navigate(NavigationRoute.Secondary.AddWord.route)
            }

            is DictionaryDetailsEvent.LoadWords -> {
                viewModel.handleEvent(event)
            }

            is DictionaryDetailsEvent.DeleteWordClicked -> {
                viewModel.handleEvent(event)
            }

            DictionaryDetailsEvent.OnBackClicked -> {
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun DictionaryDetailsScreenRoot(
    modifier: Modifier = Modifier,
    state: DictionaryDetailsState,
    onEvent: (DictionaryDetailsEvent) -> Unit,
) {
    Box(
        modifier = modifier,
    ) {
        when (state) {
            is DictionaryDetailsState.Empty -> {
                onEvent(DictionaryDetailsEvent.OnBackClicked)
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
        repeat(LOADING_SKELETONS_COUNT) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.TopStart,
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(LOADING_SKELETON_TITLE_WIDTH_PERCENT)
                        .padding(start = 32.dp, top = 16.dp)
                        .height(18.dp),
                    shape = RoundedCornerShape(4.dp),
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(LOADING_SKELETON_SUBTITLE_WIDTH_PERCENT)
                        .padding(start = 32.dp, top = 42.dp)
                        .height(18.dp),
                    shape = RoundedCornerShape(4.dp),
                )
            }
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
    val words = remember(state.words) {
        state.words
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .animateContentSize(),
    ) {
        items(
            items = words,
            key = { it.id },
        ) { item ->
            Box(
                modifier = Modifier.animateItem(),
            ) {
                WordListItem(uiItem = item, onEvent = onEvent)
            }
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
@Preview(
    name = "Landscape",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 640,
    heightDp = 360,
)
@Composable
private fun PreviewWordListScreen() {
    val sampleWords = listOf(
        WordUiModel(
            id = "1",
            text = "lernen",
            translations = "learn",
            examples = "Ich lerne Deutsch.",
            partOfSpeech = PartOfSpeech.VERB.toString(),
        ),
        WordUiModel(
            id = "2",
            text = "Haus",
            translations = "house, home",
            examples = "Das ist mein Haus.",
            partOfSpeech = PartOfSpeech.NOUN.toString(),
        ),
        WordUiModel(
            id = "3",
            text = "lernen",
            translations = "learn",
            examples = "Ich lerne Deutsch.",
            partOfSpeech = PartOfSpeech.VERB.toString(),
        ),
        WordUiModel(
            id = "4",
            text = "Haus",
            translations = "house, home",
            examples = "Das ist mein Haus.",
            partOfSpeech = PartOfSpeech.NOUN.toString(),
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
private fun PreviewLoadingScreen() {
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
private fun PreviewErrorScreen() {
    VocabriTheme {
        DictionaryDetailsScreenRoot(
            state = DictionaryDetailsState.Error(
                titleId = R.string.verbs,
                message = "Network error",
            ),
        ) {}
    }
}
