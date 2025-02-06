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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.components.ChipsFlowRow
import com.vocabri.ui.components.TextFieldWithButton
import com.vocabri.ui.screens.addword.viewmodel.AddWordEvent
import com.vocabri.ui.screens.addword.viewmodel.AddWordState
import com.vocabri.ui.theme.VocabriTheme

@Composable
fun ExampleTextField(modifier: Modifier = Modifier, state: AddWordState.Editing, onEvent: (AddWordEvent) -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextFieldWithButton(
            value = state.currentExample,
            placeholderResId = R.string.add_example_placeholder,
            buttonContentDescriptionResId = R.string.add_example,
            buttonVisible = state.showAddExampleButton,
            onFocusChange = { onEvent(AddWordEvent.OnExampleFieldFocusChange(it)) },
            onValueChange = { onEvent(AddWordEvent.UpdateCurrentExample(it)) },
            onAddValueClick = { onEvent(AddWordEvent.AddExample) },
        )
        if (state.examples.isNotEmpty()) {
            ChipsFlowRow(
                items = state.examples,
                removable = true,
                removeButtonContentDescriptionResId = R.string.remove_example,
                itemPrintableName = { it },
                isSelected = { true },
                onClick = { onEvent(AddWordEvent.RemoveExample(it)) },
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
private fun PreviewExampleTextField() {
    VocabriTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            item {
                ExampleTextField(
                    state = AddWordState.Editing(
                        currentExample = "Ich lerne",
                        showAddExampleButton = true,
                        examples = listOf("Ich lerne Deutsch"),
                        partOfSpeech = PartOfSpeech.VERB,
                    ),
                    onEvent = {},
                )
            }
        }
    }
}
