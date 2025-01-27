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
package com.vocabri.ui.addword

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.addword.viewmodel.AddWordEvent
import com.vocabri.ui.addword.viewmodel.AddWordState
import com.vocabri.ui.addword.viewmodel.AddWordViewModel
import com.vocabri.ui.theme.VocabriTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun AddWordScreen(viewModel: AddWordViewModel = koinViewModel(), navController: NavController) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AddWordState.Saved) {
            navController.popBackStack()
        }
    }

    AddWordScreenRoot(state, navController) { event ->
        viewModel.handleEvent(event)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordScreenRoot(state: AddWordState, navController: NavController, onEvent: (AddWordEvent) -> Unit) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_word),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            navController.popBackStack()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cancel),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when (state) {
            is AddWordState.Editing -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
                    SelectPartOfSpeech(state, onEvent)
                    WordField(state, onEvent)
                    TranslationField(state, onEvent)
                    SaveButton(focusManager, state, onEvent)
                    AnimatedVisibility(visible = state.errorMessageId != null) {
                        Text(
                            text = state.errorMessageId?.let { stringResource(it) } ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }

            AddWordState.Saved -> {
                // Do nothing
            }

            is AddWordState.Error -> TODO()
        }
    }
}

@Composable
fun WordField(state: AddWordState.Editing, onEvent: (AddWordEvent) -> Unit) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.text,
        onValueChange = { onEvent(AddWordEvent.UpdateText(it)) },
        placeholder = {
            Text(
                text = stringResource(R.string.word_text),
                style = MaterialTheme.typography.labelLarge,
            )
        },
        textStyle = MaterialTheme.typography.labelLarge,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslationField(state: AddWordState.Editing, onEvent: (AddWordEvent) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            TextField(
                modifier = Modifier
                    .weight(1.0f)
                    .onFocusChanged { focusState ->
                        onEvent(AddWordEvent.OnTranslationFieldFocusChange(focusState.isFocused))
                    },
                value = state.currentTranslation,
                onValueChange = { onEvent(AddWordEvent.UpdateCurrentTranslation(it)) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.enter_translation),
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                textStyle = MaterialTheme.typography.labelLarge,
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onEvent(AddWordEvent.AddTranslation) },
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
            )

            AnimatedVisibility(visible = state.showAddTranslationButton) {
                IconButton(
                    onClick = { onEvent(AddWordEvent.AddTranslation) },
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp),
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_translation),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            state.translations.forEach { translation ->
                TextButton(
                    onClick = { onEvent(AddWordEvent.RemoveTranslation(translation)) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = translation,
                            style = MaterialTheme.typography.labelSmall,
                        )
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(16.dp),
                            imageVector = Icons.Filled.Clear,
                            contentDescription = stringResource(R.string.remove_translation),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectPartOfSpeech(state: AddWordState.Editing, onEvent: (AddWordEvent) -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        PartOfSpeech.entries.forEach { partOfSpeech ->
            val isSelected = state.partOfSpeech == partOfSpeech

            TextButton(
                onClick = {
                    onEvent(AddWordEvent.UpdatePartOfSpeech(partOfSpeech))
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            ) {
                // TODO: make unified capitalize method
                Text(
                    text = partOfSpeech.name.lowercase()
                        .replaceFirstChar {
                            if (it.isLowerCase()) {
                                it.titlecase(
                                    Locale.getDefault(),
                                )
                            } else {
                                it.toString()
                            }
                        },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight(700),
                    ),
                )
            }
        }
    }
}

@Composable
private fun SaveButton(focusManager: FocusManager, state: AddWordState.Editing, onEvent: (AddWordEvent) -> Unit) {
    val saveButtonAlpha by animateFloatAsState(if (state.isSaveButtonEnabled) 1f else 0.7f)

    Button(
        onClick = {
            focusManager.clearFocus()
            onEvent(AddWordEvent.SaveWord)
        },
        enabled = state.isSaveButtonEnabled,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .alpha(saveButtonAlpha),
    ) {
        Text(
            text = stringResource(R.string.save),
            style = MaterialTheme.typography.labelLarge,
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
fun PreviewAddWordScreenEditing() {
    val navController = rememberNavController()
    VocabriTheme {
        AddWordScreenRoot(
            state = AddWordState.Editing(
                text = "lernen",
                translations = listOf("study", "learn"),
                partOfSpeech = PartOfSpeech.VERB,
            ),
            navController = navController,
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
fun PreviewAddWordScreenEditingEmpty() {
    val navController = rememberNavController()
    VocabriTheme {
        AddWordScreenRoot(
            state = AddWordState.Editing(),
            navController = navController,
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
fun PreviewAddWordScreenError() {
    val navController = rememberNavController()
    VocabriTheme {
        AddWordScreenRoot(
            state = AddWordState.Editing(errorMessageId = R.string.add_word_empty_field),
            navController = navController,
            onEvent = {},
        )
    }
}
