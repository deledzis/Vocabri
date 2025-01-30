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
package com.vocabri.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.vocabri.ui.theme.color.backgroundDark
import com.vocabri.ui.theme.color.backgroundLight
import com.vocabri.ui.theme.color.errorContainerDark
import com.vocabri.ui.theme.color.errorContainerLight
import com.vocabri.ui.theme.color.errorDark
import com.vocabri.ui.theme.color.errorLight
import com.vocabri.ui.theme.color.inverseOnSurfaceDark
import com.vocabri.ui.theme.color.inverseOnSurfaceLight
import com.vocabri.ui.theme.color.inversePrimaryDark
import com.vocabri.ui.theme.color.inversePrimaryLight
import com.vocabri.ui.theme.color.inverseSurfaceDark
import com.vocabri.ui.theme.color.inverseSurfaceLight
import com.vocabri.ui.theme.color.onBackgroundDark
import com.vocabri.ui.theme.color.onBackgroundLight
import com.vocabri.ui.theme.color.onErrorContainerDark
import com.vocabri.ui.theme.color.onErrorContainerLight
import com.vocabri.ui.theme.color.onErrorDark
import com.vocabri.ui.theme.color.onErrorLight
import com.vocabri.ui.theme.color.onPrimaryContainerDark
import com.vocabri.ui.theme.color.onPrimaryContainerLight
import com.vocabri.ui.theme.color.onPrimaryDark
import com.vocabri.ui.theme.color.onPrimaryLight
import com.vocabri.ui.theme.color.onSecondaryContainerDark
import com.vocabri.ui.theme.color.onSecondaryContainerLight
import com.vocabri.ui.theme.color.onSecondaryDark
import com.vocabri.ui.theme.color.onSecondaryLight
import com.vocabri.ui.theme.color.onSurfaceDark
import com.vocabri.ui.theme.color.onSurfaceLight
import com.vocabri.ui.theme.color.onSurfaceVariantDark
import com.vocabri.ui.theme.color.onSurfaceVariantLight
import com.vocabri.ui.theme.color.onTertiaryContainerDark
import com.vocabri.ui.theme.color.onTertiaryContainerLight
import com.vocabri.ui.theme.color.onTertiaryDark
import com.vocabri.ui.theme.color.onTertiaryLight
import com.vocabri.ui.theme.color.outlineDark
import com.vocabri.ui.theme.color.outlineLight
import com.vocabri.ui.theme.color.outlineVariantDark
import com.vocabri.ui.theme.color.outlineVariantLight
import com.vocabri.ui.theme.color.primaryContainerDark
import com.vocabri.ui.theme.color.primaryContainerLight
import com.vocabri.ui.theme.color.primaryDark
import com.vocabri.ui.theme.color.primaryLight
import com.vocabri.ui.theme.color.scrimDark
import com.vocabri.ui.theme.color.scrimLight
import com.vocabri.ui.theme.color.secondaryContainerDark
import com.vocabri.ui.theme.color.secondaryContainerLight
import com.vocabri.ui.theme.color.secondaryDark
import com.vocabri.ui.theme.color.secondaryLight
import com.vocabri.ui.theme.color.surfaceBrightDark
import com.vocabri.ui.theme.color.surfaceBrightLight
import com.vocabri.ui.theme.color.surfaceContainerDark
import com.vocabri.ui.theme.color.surfaceContainerHighDark
import com.vocabri.ui.theme.color.surfaceContainerHighLight
import com.vocabri.ui.theme.color.surfaceContainerHighestDark
import com.vocabri.ui.theme.color.surfaceContainerHighestLight
import com.vocabri.ui.theme.color.surfaceContainerLight
import com.vocabri.ui.theme.color.surfaceContainerLowDark
import com.vocabri.ui.theme.color.surfaceContainerLowLight
import com.vocabri.ui.theme.color.surfaceContainerLowestDark
import com.vocabri.ui.theme.color.surfaceContainerLowestLight
import com.vocabri.ui.theme.color.surfaceDark
import com.vocabri.ui.theme.color.surfaceDimDark
import com.vocabri.ui.theme.color.surfaceDimLight
import com.vocabri.ui.theme.color.surfaceLight
import com.vocabri.ui.theme.color.surfaceVariantDark
import com.vocabri.ui.theme.color.surfaceVariantLight
import com.vocabri.ui.theme.color.tertiaryContainerDark
import com.vocabri.ui.theme.color.tertiaryContainerLight
import com.vocabri.ui.theme.color.tertiaryDark
import com.vocabri.ui.theme.color.tertiaryLight

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun VocabriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) darkScheme else lightScheme
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
