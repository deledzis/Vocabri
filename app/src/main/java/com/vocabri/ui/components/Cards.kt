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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vocabri.ui.components.modifier.shadow
import com.vocabri.ui.theme.VocabriTheme
import com.vocabri.ui.theme.color.GradientColors.tertiaryGradient
import com.vocabri.ui.theme.color.ShadowColors

object Cards {

    @Composable
    fun Solid(
        modifier: Modifier = Modifier,
        shape: Shape = RoundedCornerShape(16.dp),
        color: Color = MaterialTheme.colorScheme.primaryContainer,
        rippleColor: Color = Color.Gray,
        onClick: () -> Unit,
        content: @Composable () -> Unit,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isDarkTheme = isSystemInDarkTheme()
        val shadowModifier = Modifier
            .shadow(
                color = ShadowColors.CardColor,
                offsetX = 6.dp,
                offsetY = 6.dp,
                blurRadius = 10.dp,
            )

        Box(
            modifier = Modifier
                .then(if (isDarkTheme) Modifier else shadowModifier)
                .then(modifier)
                .clip(shape = shape)
                .background(color = color)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = rippleColor),
                ) { onClick() },
        ) {
            content()
        }
    }

    @Composable
    fun Gradient(
        modifier: Modifier = Modifier,
        gradient: List<Color>,
        shape: Shape = RoundedCornerShape(16.dp),
        shadowColor: Color = ShadowColors.TertiaryColor,
        onClick: () -> Unit,
        content: @Composable () -> Unit,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isDarkTheme = isSystemInDarkTheme()
        val shadowModifier = Modifier
            .shadow(
                color = shadowColor,
                offsetX = 6.dp,
                offsetY = 6.dp,
                blurRadius = 10.dp,
            )
        Box(
            modifier = Modifier
                .then(if (isDarkTheme) Modifier else shadowModifier)
                .then(modifier)
                .clip(shape = shape)
                .background(brush = Brush.horizontalGradient(colors = gradient))
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = shadowColor),
                ) { onClick() },
        ) {
            content()
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
@Composable
private fun PreviewWhiteCard() {
    VocabriTheme {
        Box(
            modifier = Modifier
                .size(400.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Cards.Solid(
                modifier = Modifier
                    .padding(all = 16.dp),
                onClick = {},
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Text("test")
                }
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
@Composable
private fun PreviewTertiaryGradientCard() {
    VocabriTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Cards.Gradient(
                modifier = Modifier
                    .padding(all = 16.dp),
                gradient = MaterialTheme.colorScheme.tertiaryGradient,
                onClick = {},
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Text("test")
                }
            }
        }
    }
}
