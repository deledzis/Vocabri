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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.logger.logger
import com.vocabri.ui.components.Buttons
import com.vocabri.ui.components.ShimmerEffect
import com.vocabri.ui.dictionary.components.WordGroupCard
import com.vocabri.ui.dictionary.components.WordGroupHighlightedCard
import com.vocabri.ui.dictionary.model.WordGroupUiModel
import com.vocabri.ui.dictionary.viewmodel.DictionaryEvent
import com.vocabri.ui.dictionary.viewmodel.DictionaryState
import com.vocabri.ui.dictionary.viewmodel.DictionaryViewModel
import com.vocabri.ui.navigation.NavigationRoute
import com.vocabri.ui.theme.VocabriTheme
import org.koin.androidx.compose.koinViewModel

private const val CATEGORY_GRID_WIDTH_DP = 200 // in dp
private const val LOADING_SKELETONS_COUNT = 4
private const val LOADING_SKELETON_TITLE_WIDTH_PERCENT = 0.5f
private const val LOADING_SKELETON_SUBTITLE_WIDTH_PERCENT = 0.7f

@Composable
fun DictionaryScreen(
    modifier: Modifier = Modifier,
    viewModel: DictionaryViewModel = koinViewModel(),
    navController: NavController,
) {
    val log = logger("DictionaryScreen")
    val state by viewModel.state.collectAsState()

    log.i { "DictionaryScreen is displayed" }

    LaunchedEffect(viewModel) {
        log.i { "Triggering initial load of words" }
        viewModel.handleEvent(DictionaryEvent.LoadWords)
    }

    DictionaryScreenRoot(modifier = modifier, state = state) { event ->
        log.i { "Handling event: $event" }
        when (event) {
            is DictionaryEvent.AddWordClicked -> {
                navController.navigate(NavigationRoute.Secondary.AddWord.route)
            }

            is DictionaryEvent.LoadWords -> {
                viewModel.handleEvent(event)
            }

            is DictionaryEvent.OnGroupCardClicked -> {
                navController.navigate(
                    NavigationRoute.Secondary.DictionaryDetails.fromGroup(event.wordGroup),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreenRoot(modifier: Modifier = Modifier, state: DictionaryState, onEvent: (DictionaryEvent) -> Unit) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dictionary_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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
            Buttons.Filled(
                modifier = Modifier,
                text = stringResource(R.string.add_word),
                icon = Icons.Default.Create,
                contentDescriptionResId = R.string.add_word,
            ) { onEvent(DictionaryEvent.AddWordClicked) }
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
        repeat(LOADING_SKELETONS_COUNT) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.TopStart,
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(96.dp),
                    shape = RoundedCornerShape(16.dp),
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(LOADING_SKELETON_TITLE_WIDTH_PERCENT)
                        .padding(start = 32.dp, top = 24.dp)
                        .height(24.dp),
                    shape = RoundedCornerShape(4.dp),
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(LOADING_SKELETON_SUBTITLE_WIDTH_PERCENT)
                        .padding(start = 32.dp, top = 56.dp)
                        .height(24.dp),
                    shape = RoundedCornerShape(4.dp),
                )
            }
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
    val log = logger("DictionaryScreenGrid")

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val screenHeight = configuration.screenHeightDp
        val isLandscape = screenWidth > screenHeight

        val columnCount = screenWidth / CATEGORY_GRID_WIDTH_DP
        log.e { "columnCount: $columnCount" }

        when (isLandscape) {
            true -> {
                // show all cards in a grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = CATEGORY_GRID_WIDTH_DP.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // considering "All words" group too
                    items(state.groups.size + 1) { groupIndex ->
                        if (groupIndex == 0) {
                            WordGroupHighlightedCard(uiItem = state.allWords, onEvent = onEvent)
                        } else {
                            WordGroupCard(uiItem = state.groups[groupIndex - 1], onEvent = onEvent)
                        }
                    }
                }
            }

            false -> {
                WordGroupHighlightedCard(uiItem = state.allWords, onEvent = onEvent)
                val groupsColumns = state.groups.chunked(columnCount)
                groupsColumns.forEach { column ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        column.forEach { group ->
                            WordGroupCard(
                                modifier = Modifier.weight(1f),
                                uiItem = group,
                                onEvent = onEvent,
                            )
                        }
                    }
                }
            }
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
@Preview(
    name = "Landscape",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 640,
    heightDp = 360,
)
@Composable
private fun PreviewWordListScreen() {
    val allWords = WordGroupUiModel(
        partOfSpeech = PartOfSpeech.ALL,
        titleText = "All words",
        subtitleText = "Words: 18",
    )
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
        DictionaryScreenRoot(state = DictionaryState.GroupsLoaded(allWords = allWords, groups = sampleWordGroups)) {}
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
private fun PreviewEmptyDictionaryScreen() {
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
private fun PreviewLoadingScreen() {
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
private fun PreviewErrorScreen() {
    VocabriTheme {
        DictionaryScreenRoot(state = DictionaryState.Error(message = "Network error")) {}
    }
}
