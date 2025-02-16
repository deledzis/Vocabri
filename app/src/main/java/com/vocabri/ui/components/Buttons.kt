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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vocabri.R
import com.vocabri.ui.components.modifier.dashedBorder
import com.vocabri.ui.components.modifier.shadow
import com.vocabri.ui.theme.VocabriTheme
import com.vocabri.ui.theme.color.ShadowColors

object Buttons {

    object Transparent {

        @Composable
        operator fun invoke(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            TransparentButtonDefault(
                modifier = modifier,
                enabled = enabled,
                iconResId = iconResId,
                contentDescriptionResId = contentDescriptionResId,
                text = text,
                onClick = onClick,
            )
        }

        @Composable
        fun Underlined(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            TransparentButtonDefault(
                modifier = modifier,
                enabled = enabled,
                textDecoration = TextDecoration.Underline,
                iconResId = iconResId,
                contentDescriptionResId = contentDescriptionResId,
                text = text,
                onClick = onClick,
            )
        }

        @Composable
        fun AllCaps(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            TransparentButtonDefault(
                modifier = modifier,
                enabled = enabled,
                iconResId = iconResId,
                contentDescriptionResId = contentDescriptionResId,
                text = text.uppercase(),
                onClick = onClick,
            )
        }

        @Composable
        fun IconOnly(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int,
            @StringRes contentDescriptionResId: Int,
            onClick: () -> Unit,
        ) {
            TransparentButtonIcon(
                modifier = modifier,
                enabled = enabled,
                iconResId = iconResId,
                contentDescriptionResId = contentDescriptionResId,
                onClick = onClick,
            )
        }

        @Composable
        private fun TransparentButtonDefault(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            textDecoration: TextDecoration? = null,
            @DrawableRes iconResId: Int? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            TextButton(
                modifier = modifier,
                onClick = onClick,
                enabled = enabled,
            ) {
                if (iconResId != null) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = contentDescriptionResId?.let { id ->
                            stringResource(id)
                        },
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                }
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = textDecoration,
                    maxLines = 1,
                )
            }
        }

        @OptIn(ExperimentalFoundationApi::class)
        @Composable
        private fun TransparentButtonIcon(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            size: Dp = 48.dp,
            rippleColor: Color = Color.Gray,
            rippleRadius: Dp = 32.dp,
            onLongClick: () -> Unit = {},
            @DrawableRes iconResId: Int,
            @StringRes contentDescriptionResId: Int,
            onClick: () -> Unit,
        ) {
            Box(
                modifier = modifier
                    .width(IntrinsicSize.Max)
                    .height(IntrinsicSize.Max),
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .combinedClickable(
                            enabled = enabled,
                            interactionSource = interactionSource,
                            indication = ripple(color = rippleColor, radius = rippleRadius),
                            onLongClick = onLongClick,
                            onClick = onClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = iconResId),
                            contentDescription = stringResource(contentDescriptionResId),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }

    object Outlined {

        @Composable
        operator fun invoke(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            OutlinedButtonDefault(
                modifier = modifier,
                enabled = enabled,
                iconResId = iconResId,
                contentDescriptionResId = contentDescriptionResId,
                text = text,
                onClick = onClick,
            )
        }

        @Composable
        fun Underlined(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            OutlinedButtonDefault(
                modifier = modifier,
                enabled = enabled,
                textDecoration = TextDecoration.Underline,
                iconResId = iconResId,
                contentDescriptionResId = contentDescriptionResId,
                text = text,
                onClick = onClick,
            )
        }

        @Composable
        fun AllCaps(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            OutlinedButtonDefault(
                modifier = modifier,
                enabled = enabled,
                iconResId = iconResId,
                contentDescriptionResId = contentDescriptionResId,
                text = text.uppercase(),
                onClick = onClick,
            )
        }

        @Composable
        private fun OutlinedButtonDefault(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            textDecoration: TextDecoration? = null,
            @DrawableRes iconResId: Int? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            TextButton(
                modifier = modifier
                    .dashedBorder(
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        cornerRadiusDp = 8.dp,
                    ),
                onClick = onClick,
                enabled = enabled,
                shape = MaterialTheme.shapes.small,
            ) {
                if (iconResId != null) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = contentDescriptionResId?.let { id ->
                            stringResource(id)
                        },
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                }
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = textDecoration,
                    maxLines = 1,
                )
            }
        }
    }

    object Filled {

        @Composable
        operator fun invoke(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int? = null,
            icon: ImageVector? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            FilledButtonDefault(
                modifier = modifier,
                enabled = enabled,
                iconResId = iconResId,
                icon = icon,
                contentDescriptionResId = contentDescriptionResId,
                text = text,
                onClick = onClick,
            )
        }

        @Composable
        fun Underlined(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int? = null,
            icon: ImageVector? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            FilledButtonDefault(
                modifier = modifier,
                enabled = enabled,
                iconResId = iconResId,
                icon = icon,
                textDecoration = TextDecoration.Underline,
                contentDescriptionResId = contentDescriptionResId,
                text = text,
                onClick = onClick,
            )
        }

        @Composable
        fun AllCaps(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int? = null,
            icon: ImageVector? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            FilledButtonDefault(
                modifier = modifier,
                enabled = enabled,
                iconResId = iconResId,
                icon = icon,
                contentDescriptionResId = contentDescriptionResId,
                text = text.uppercase(),
                onClick = onClick,
            )
        }

        @Composable
        fun IconOnly(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            onLongClick: () -> Unit = {},
            @DrawableRes iconResId: Int,
            @StringRes contentDescriptionResId: Int,
            onClick: () -> Unit,
        ) {
            FilledButtonIcon(
                modifier = modifier,
                enabled = enabled,
                iconResId = iconResId,
                contentDescriptionResId = contentDescriptionResId,
                onLongClick = onLongClick,
                onClick = onClick,
            )
        }

        @Composable
        private fun FilledButtonDefault(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            @DrawableRes iconResId: Int? = null,
            icon: ImageVector? = null,
            textDecoration: TextDecoration? = null,
            @StringRes contentDescriptionResId: Int? = null,
            text: String,
            onClick: () -> Unit,
        ) {
            Box(
                modifier = modifier
                    .width(IntrinsicSize.Max)
                    .height(IntrinsicSize.Max),
            ) {
                if (enabled) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(
                                color = ShadowColors.TertiaryColor,
                                offsetX = 6.dp,
                                offsetY = 6.dp,
                                blurRadius = 16.dp,
                            ),
                    )
                }

//                val gradient = Brush.verticalGradient(
//                    colors = MaterialTheme.colorScheme.tertiaryGradient,
//                )
                Button(
                    colors = ButtonDefaults.buttonColors(contentColor = Color.Transparent),
                    onClick = onClick,
                    enabled = enabled,
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(), // use 0 padding
                ) {
                    Row(
                        modifier = Modifier
                            .then(
                                if (enabled) {
                                    Modifier.background(MaterialTheme.colorScheme.tertiary)
                                } else {
                                    Modifier.background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                                },
                            )
                            .padding(
                                PaddingValues(
                                    start = 48.dp,
                                    top = 16.dp,
                                    end = 48.dp,
                                    bottom = 16.dp,
                                ),
                            )
                            .then(modifier),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        if (iconResId != null) {
                            // Inner content including an icon and a text label
                            Icon(
                                painter = painterResource(id = iconResId),
                                contentDescription = contentDescriptionResId?.let { id ->
                                    stringResource(id)
                                },
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onTertiary,
                            )
                            Spacer(Modifier.size(16.dp))
                        }
                        if (icon != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = contentDescriptionResId?.let { id ->
                                    stringResource(id)
                                },
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onTertiary,
                            )
                            Spacer(Modifier.size(16.dp))
                        }
                        Text(
                            modifier = Modifier.background(Color.Transparent),
                            text = text,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onTertiary,
                            textDecoration = textDecoration,
                            maxLines = 1,
                        )
                    }
                }
            }
        }

        @OptIn(ExperimentalFoundationApi::class)
        @Composable
        private fun FilledButtonIcon(
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            size: Dp = 48.dp,
            @DrawableRes iconResId: Int,
            @StringRes contentDescriptionResId: Int,
            rippleColor: Color = Color.Gray,
            rippleRadius: Dp = 32.dp,
            onLongClick: () -> Unit = {},
            onClick: () -> Unit,
        ) {
            Box(
                modifier = modifier
                    .width(IntrinsicSize.Max)
                    .height(IntrinsicSize.Max),
            ) {
                if (enabled) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(
                                color = ShadowColors.TertiaryColor,
                                offsetX = 6.dp,
                                offsetY = 6.dp,
                                blurRadius = 10.dp,
                            ),
                    )
                }

//                val gradient = Brush.verticalGradient(
//                    colors = MaterialTheme.colorScheme.tertiaryGradient,
//                )
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .then(
                            if (enabled) {
                                Modifier.background(MaterialTheme.colorScheme.tertiary)
                            } else {
                                Modifier.background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                            },
                        )
                        .combinedClickable(
                            interactionSource = interactionSource,
                            indication = ripple(color = rippleColor, radius = rippleRadius),
                            onLongClick = onLongClick,
                            onClick = onClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = iconResId),
                            contentDescription = stringResource(id = contentDescriptionResId),
                            tint = MaterialTheme.colorScheme.onTertiary,
                        )
                    }
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
private fun PreviewTransparentButtons() {
    VocabriTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        ) {
            Buttons.Transparent(
                text = "Добавить таблетку",
                iconResId = R.drawable.ic_plus,
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            Buttons.Transparent.Underlined(
                modifier = Modifier.fillMaxWidth(),
                text = "Больше информации",
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            Buttons.Transparent.AllCaps(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                text = "Удалить данные",
                iconResId = R.drawable.ic_chevron_right,
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            Buttons.Transparent.IconOnly(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                contentDescriptionResId = R.string.navigation_description_plus_button,
                iconResId = R.drawable.ic_chevron_right,
            ) {}
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
private fun PreviewOutlinedButtons() {
    VocabriTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        ) {
            Buttons.Outlined(
                text = "Добавить таблетку",
                iconResId = R.drawable.ic_plus,
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            Buttons.Outlined.Underlined(
                modifier = Modifier.fillMaxWidth(),
                text = "Больше информации",
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            Buttons.Outlined.AllCaps(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                text = "Удалить данные",
                iconResId = R.drawable.ic_chevron_right,
            ) {}
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
private fun PreviewFilledButtons() {
    VocabriTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        ) {
            Buttons.Filled(
                text = "Добавить таблетку",
                iconResId = R.drawable.ic_plus,
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            Buttons.Filled.Underlined(
                modifier = Modifier.fillMaxWidth(),
                text = "Больше информации",
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            Buttons.Filled.AllCaps(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                text = "Удалить данные",
                iconResId = R.drawable.ic_chevron_right,
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            Buttons.Filled.IconOnly(
                iconResId = R.drawable.ic_plus,
                contentDescriptionResId = R.string.navigation_description_plus_button,
            ) {}
        }
    }
}
