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
package com.vocabri.ui.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vocabri.R
import com.vocabri.ui.theme.VocabriTheme

@Composable
fun TextFieldWithButton(
    modifier: Modifier = Modifier,
    value: String,
    @StringRes placeholderResId: Int,
    @StringRes buttonContentDescriptionResId: Int,
    buttonVisible: Boolean,
    onValueChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onAddValueClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            modifier = Modifier
                .weight(1.0f)
                .onFocusChanged { focusState ->
                    onFocusChange(focusState.isFocused)
                },
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = stringResource(placeholderResId),
                    style = MaterialTheme.typography.labelLarge,
                )
            },
            textStyle = MaterialTheme.typography.labelLarge,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onAddValueClick() },
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
        )

        AnimatedVisibility(visible = buttonVisible) {
            IconButton(
                onClick = { onAddValueClick() },
                modifier = Modifier
                    .padding(start = 4.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .background(
                        MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(16.dp),
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(buttonContentDescriptionResId),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
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
private fun PreviewTextFieldWithButton() {
    VocabriTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                TextFieldWithButton(
                    value = "Some text",
                    placeholderResId = R.string.add_translation_placeholder,
                    buttonContentDescriptionResId = R.string.add_translation,
                    buttonVisible = true,
                    onValueChange = {},
                    onFocusChange = {},
                    onAddValueClick = {},
                )
            }
            item {
                TextFieldWithButton(
                    value = "",
                    placeholderResId = R.string.add_example_placeholder,
                    buttonContentDescriptionResId = R.string.add_example,
                    buttonVisible = true,
                    onValueChange = {},
                    onFocusChange = {},
                    onAddValueClick = {},
                )
            }
        }
    }
}
