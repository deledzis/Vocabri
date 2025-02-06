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
package com.vocabri.ui.screens.addword.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.components.Buttons
import com.vocabri.ui.screens.addword.viewmodel.AddWordEvent
import com.vocabri.ui.screens.addword.viewmodel.AddWordState
import com.vocabri.ui.theme.VocabriTheme

@Composable
fun SaveBottomBar(
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    state: AddWordState.Editing,
    onEvent: (AddWordEvent) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight(),
    ) {
        SaveButton(focusManager = focusManager, state = state, onEvent = onEvent)

        AnimatedVisibility(visible = state.errorMessageId != null) {
            Text(
                text = state.errorMessageId?.let { stringResource(it) }.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun SaveButton(
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    state: AddWordState.Editing,
    onEvent: (AddWordEvent) -> Unit,
) {
    Buttons.Filled(
        modifier = modifier
            .fillMaxWidth(),
        enabled = state.isSaveButtonEnabled,
        contentDescriptionResId = R.string.save,
        text = stringResource(R.string.save),
    ) {
        focusManager.clearFocus()
        onEvent(AddWordEvent.SaveWord)
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
private fun PreviewSaveBottomBar() {
    val focusManager = LocalFocusManager.current
    VocabriTheme {
        SaveBottomBar(
            state = AddWordState.Editing(
                text = "lernen",
                translations = listOf("study", "learn"),
                partOfSpeech = PartOfSpeech.VERB,
            ),
            focusManager = focusManager,
            onEvent = {},
        )
    }
}
