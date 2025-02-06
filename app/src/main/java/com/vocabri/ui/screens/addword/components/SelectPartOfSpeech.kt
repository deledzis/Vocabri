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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.components.ChipsFlowRow
import com.vocabri.ui.screens.addword.viewmodel.AddWordEvent
import com.vocabri.ui.screens.addword.viewmodel.AddWordState
import com.vocabri.ui.screens.dictionary.model.toLabelResId
import com.vocabri.ui.theme.VocabriTheme

@Composable
fun SelectPartOfSpeech(modifier: Modifier = Modifier, state: AddWordState.Editing, onEvent: (AddWordEvent) -> Unit) {
    ChipsFlowRow(
        modifier = modifier,
        items = PartOfSpeech.noAll,
        removable = false,
        itemPrintableName = { stringResource(it.toLabelResId) },
        isSelected = { state.partOfSpeech == it },
        onClick = { onEvent(AddWordEvent.UpdatePartOfSpeech(it)) },
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
private fun PreviewSelectPartOfSpeech() {
    VocabriTheme {
        SelectPartOfSpeech(
            state = AddWordState.Editing(
                text = "lernen",
                translations = listOf("study", "learn"),
                partOfSpeech = PartOfSpeech.VERB,
            ),
            onEvent = {},
        )
    }
}
