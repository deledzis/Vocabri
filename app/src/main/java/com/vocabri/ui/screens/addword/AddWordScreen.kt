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
package com.vocabri.ui.screens.addword

import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.screens.addword.components.AdjectiveAdverbFields
import com.vocabri.ui.screens.addword.components.ExampleTextField
import com.vocabri.ui.screens.addword.components.NounFields
import com.vocabri.ui.screens.addword.components.SaveBottomBar
import com.vocabri.ui.screens.addword.components.SelectPartOfSpeech
import com.vocabri.ui.screens.addword.components.TranslationTextField
import com.vocabri.ui.screens.addword.components.VerbFields
import com.vocabri.ui.screens.addword.components.WordTextField
import com.vocabri.ui.screens.addword.viewmodel.AddWordContract
import com.vocabri.ui.screens.addword.viewmodel.AddWordViewModel
import com.vocabri.ui.theme.VocabriTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddWordScreen(
    modifier: Modifier = Modifier,
    viewModel: AddWordViewModel = koinViewModel(),
    focusManager: FocusManager,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                AddWordContract.UiEffect.WordSaved -> onNavigateBack()
                is AddWordContract.UiEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        AddWordScreenRoot(
            modifier = Modifier.padding(padding),
            state = uiState,
            focusManager = focusManager,
            onEvent = viewModel::onEvent,
        )
    }
}

@Composable
fun AddWordScreenRoot(
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    state: AddWordContract.UiState,
    onEvent: (AddWordContract.UiEvent) -> Unit,
) {
    when (state) {
        is AddWordContract.UiState.Editing -> AddWordScreenContent(modifier, focusManager, state, onEvent)
    }
}

@Composable
private fun AddWordScreenContent(
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    state: AddWordContract.UiState.Editing,
    onEvent: (AddWordContract.UiEvent) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    },
                )
            },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Common fields
        item {
            SelectPartOfSpeech(state = state, onEvent = onEvent)
        }
        item {
            WordTextField(state = state, onEvent = onEvent)
        }
        item {
            TranslationTextField(state = state, onEvent = onEvent)
        }
        item {
            ExampleTextField(state = state, onEvent = onEvent)
        }
        item {
            Crossfade(targetState = state.partOfSpeech) { partOfSpeech ->
                when (partOfSpeech) {
                    PartOfSpeech.NOUN -> NounFields(state = state, onEvent = onEvent)
                    PartOfSpeech.VERB -> VerbFields(state = state, onEvent = onEvent)
                    PartOfSpeech.ADJECTIVE -> AdjectiveAdverbFields(state = state, onEvent = onEvent)
                    PartOfSpeech.ADVERB -> AdjectiveAdverbFields(state = state, onEvent = onEvent)
                    PartOfSpeech.ALL -> error("PartOfSpeech.ALL is not allowed in this context!")
                }
            }
        }
        item {
            SaveBottomBar(
                focusManager = focusManager,
                state = state,
                onEvent = onEvent,
            )
        }
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
@Preview(
    name = "Landscape",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 640,
    heightDp = 360,
)
@Composable
private fun PreviewAddWordScreenEditing() {
    val focusManager = LocalFocusManager.current
    VocabriTheme {
        AddWordScreenRoot(
            state = AddWordContract.UiState.Editing(
                text = "lernen",
                partOfSpeech = PartOfSpeech.VERB,
                translations = listOf("study", "learn"),
                examples = listOf("I study", "I learn"),
                conjugations = listOf("lerne", "lernst"),
                managements = listOf("auf + D"),
                currentConjugation = "test",
                currentManagement = "",
            ),
            focusManager = focusManager,
            onEvent = {},
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
private fun PreviewAddWordScreenEditingEmpty() {
    val focusManager = LocalFocusManager.current
    VocabriTheme {
        AddWordScreenRoot(
            state = AddWordContract.UiState.Editing(),
            focusManager = focusManager,
            onEvent = {},
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
private fun PreviewAddWordScreenError() {
    val focusManager = LocalFocusManager.current
    VocabriTheme {
        AddWordScreenRoot(
            state = AddWordContract.UiState.Editing(
                partOfSpeech = PartOfSpeech.ADJECTIVE,
                errorMessageId = R.string.add_word_empty_field,
            ),
            focusManager = focusManager,
            onEvent = {},
        )
    }
}
