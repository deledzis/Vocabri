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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vocabri.ui.theme.color.backgroundDark
import com.vocabri.ui.theme.color.backgroundDarkHighContrast
import com.vocabri.ui.theme.color.backgroundDarkMediumContrast
import com.vocabri.ui.theme.color.backgroundLight
import com.vocabri.ui.theme.color.backgroundLightHighContrast
import com.vocabri.ui.theme.color.backgroundLightMediumContrast
import com.vocabri.ui.theme.color.errorContainerDark
import com.vocabri.ui.theme.color.errorContainerDarkHighContrast
import com.vocabri.ui.theme.color.errorContainerDarkMediumContrast
import com.vocabri.ui.theme.color.errorContainerLight
import com.vocabri.ui.theme.color.errorContainerLightHighContrast
import com.vocabri.ui.theme.color.errorContainerLightMediumContrast
import com.vocabri.ui.theme.color.errorDark
import com.vocabri.ui.theme.color.errorDarkHighContrast
import com.vocabri.ui.theme.color.errorDarkMediumContrast
import com.vocabri.ui.theme.color.errorLight
import com.vocabri.ui.theme.color.errorLightHighContrast
import com.vocabri.ui.theme.color.errorLightMediumContrast
import com.vocabri.ui.theme.color.inverseOnSurfaceDark
import com.vocabri.ui.theme.color.inverseOnSurfaceDarkHighContrast
import com.vocabri.ui.theme.color.inverseOnSurfaceDarkMediumContrast
import com.vocabri.ui.theme.color.inverseOnSurfaceLight
import com.vocabri.ui.theme.color.inverseOnSurfaceLightHighContrast
import com.vocabri.ui.theme.color.inverseOnSurfaceLightMediumContrast
import com.vocabri.ui.theme.color.inversePrimaryDark
import com.vocabri.ui.theme.color.inversePrimaryDarkHighContrast
import com.vocabri.ui.theme.color.inversePrimaryDarkMediumContrast
import com.vocabri.ui.theme.color.inversePrimaryLight
import com.vocabri.ui.theme.color.inversePrimaryLightHighContrast
import com.vocabri.ui.theme.color.inversePrimaryLightMediumContrast
import com.vocabri.ui.theme.color.inverseSurfaceDark
import com.vocabri.ui.theme.color.inverseSurfaceDarkHighContrast
import com.vocabri.ui.theme.color.inverseSurfaceDarkMediumContrast
import com.vocabri.ui.theme.color.inverseSurfaceLight
import com.vocabri.ui.theme.color.inverseSurfaceLightHighContrast
import com.vocabri.ui.theme.color.inverseSurfaceLightMediumContrast
import com.vocabri.ui.theme.color.onBackgroundDark
import com.vocabri.ui.theme.color.onBackgroundDarkHighContrast
import com.vocabri.ui.theme.color.onBackgroundDarkMediumContrast
import com.vocabri.ui.theme.color.onBackgroundLight
import com.vocabri.ui.theme.color.onBackgroundLightHighContrast
import com.vocabri.ui.theme.color.onBackgroundLightMediumContrast
import com.vocabri.ui.theme.color.onErrorContainerDark
import com.vocabri.ui.theme.color.onErrorContainerDarkHighContrast
import com.vocabri.ui.theme.color.onErrorContainerDarkMediumContrast
import com.vocabri.ui.theme.color.onErrorContainerLight
import com.vocabri.ui.theme.color.onErrorContainerLightHighContrast
import com.vocabri.ui.theme.color.onErrorContainerLightMediumContrast
import com.vocabri.ui.theme.color.onErrorDark
import com.vocabri.ui.theme.color.onErrorDarkHighContrast
import com.vocabri.ui.theme.color.onErrorDarkMediumContrast
import com.vocabri.ui.theme.color.onErrorLight
import com.vocabri.ui.theme.color.onErrorLightHighContrast
import com.vocabri.ui.theme.color.onErrorLightMediumContrast
import com.vocabri.ui.theme.color.onPrimaryContainerDark
import com.vocabri.ui.theme.color.onPrimaryContainerDarkHighContrast
import com.vocabri.ui.theme.color.onPrimaryContainerDarkMediumContrast
import com.vocabri.ui.theme.color.onPrimaryContainerLight
import com.vocabri.ui.theme.color.onPrimaryContainerLightHighContrast
import com.vocabri.ui.theme.color.onPrimaryContainerLightMediumContrast
import com.vocabri.ui.theme.color.onPrimaryDark
import com.vocabri.ui.theme.color.onPrimaryDarkHighContrast
import com.vocabri.ui.theme.color.onPrimaryDarkMediumContrast
import com.vocabri.ui.theme.color.onPrimaryLight
import com.vocabri.ui.theme.color.onPrimaryLightHighContrast
import com.vocabri.ui.theme.color.onPrimaryLightMediumContrast
import com.vocabri.ui.theme.color.onSecondaryContainerDark
import com.vocabri.ui.theme.color.onSecondaryContainerDarkHighContrast
import com.vocabri.ui.theme.color.onSecondaryContainerDarkMediumContrast
import com.vocabri.ui.theme.color.onSecondaryContainerLight
import com.vocabri.ui.theme.color.onSecondaryContainerLightHighContrast
import com.vocabri.ui.theme.color.onSecondaryContainerLightMediumContrast
import com.vocabri.ui.theme.color.onSecondaryDark
import com.vocabri.ui.theme.color.onSecondaryDarkHighContrast
import com.vocabri.ui.theme.color.onSecondaryDarkMediumContrast
import com.vocabri.ui.theme.color.onSecondaryLight
import com.vocabri.ui.theme.color.onSecondaryLightHighContrast
import com.vocabri.ui.theme.color.onSecondaryLightMediumContrast
import com.vocabri.ui.theme.color.onSurfaceDark
import com.vocabri.ui.theme.color.onSurfaceDarkHighContrast
import com.vocabri.ui.theme.color.onSurfaceDarkMediumContrast
import com.vocabri.ui.theme.color.onSurfaceLight
import com.vocabri.ui.theme.color.onSurfaceLightHighContrast
import com.vocabri.ui.theme.color.onSurfaceLightMediumContrast
import com.vocabri.ui.theme.color.onSurfaceVariantDark
import com.vocabri.ui.theme.color.onSurfaceVariantDarkHighContrast
import com.vocabri.ui.theme.color.onSurfaceVariantDarkMediumContrast
import com.vocabri.ui.theme.color.onSurfaceVariantLight
import com.vocabri.ui.theme.color.onSurfaceVariantLightHighContrast
import com.vocabri.ui.theme.color.onSurfaceVariantLightMediumContrast
import com.vocabri.ui.theme.color.onTertiaryContainerDark
import com.vocabri.ui.theme.color.onTertiaryContainerDarkHighContrast
import com.vocabri.ui.theme.color.onTertiaryContainerDarkMediumContrast
import com.vocabri.ui.theme.color.onTertiaryContainerLight
import com.vocabri.ui.theme.color.onTertiaryContainerLightHighContrast
import com.vocabri.ui.theme.color.onTertiaryContainerLightMediumContrast
import com.vocabri.ui.theme.color.onTertiaryDark
import com.vocabri.ui.theme.color.onTertiaryDarkHighContrast
import com.vocabri.ui.theme.color.onTertiaryDarkMediumContrast
import com.vocabri.ui.theme.color.onTertiaryLight
import com.vocabri.ui.theme.color.onTertiaryLightHighContrast
import com.vocabri.ui.theme.color.onTertiaryLightMediumContrast
import com.vocabri.ui.theme.color.outlineDark
import com.vocabri.ui.theme.color.outlineDarkHighContrast
import com.vocabri.ui.theme.color.outlineDarkMediumContrast
import com.vocabri.ui.theme.color.outlineLight
import com.vocabri.ui.theme.color.outlineLightHighContrast
import com.vocabri.ui.theme.color.outlineLightMediumContrast
import com.vocabri.ui.theme.color.outlineVariantDark
import com.vocabri.ui.theme.color.outlineVariantDarkHighContrast
import com.vocabri.ui.theme.color.outlineVariantDarkMediumContrast
import com.vocabri.ui.theme.color.outlineVariantLight
import com.vocabri.ui.theme.color.outlineVariantLightHighContrast
import com.vocabri.ui.theme.color.outlineVariantLightMediumContrast
import com.vocabri.ui.theme.color.primaryContainerDark
import com.vocabri.ui.theme.color.primaryContainerDarkHighContrast
import com.vocabri.ui.theme.color.primaryContainerDarkMediumContrast
import com.vocabri.ui.theme.color.primaryContainerLight
import com.vocabri.ui.theme.color.primaryContainerLightHighContrast
import com.vocabri.ui.theme.color.primaryContainerLightMediumContrast
import com.vocabri.ui.theme.color.primaryDark
import com.vocabri.ui.theme.color.primaryDarkHighContrast
import com.vocabri.ui.theme.color.primaryDarkMediumContrast
import com.vocabri.ui.theme.color.primaryLight
import com.vocabri.ui.theme.color.primaryLightHighContrast
import com.vocabri.ui.theme.color.primaryLightMediumContrast
import com.vocabri.ui.theme.color.scrimDark
import com.vocabri.ui.theme.color.scrimDarkHighContrast
import com.vocabri.ui.theme.color.scrimDarkMediumContrast
import com.vocabri.ui.theme.color.scrimLight
import com.vocabri.ui.theme.color.scrimLightHighContrast
import com.vocabri.ui.theme.color.scrimLightMediumContrast
import com.vocabri.ui.theme.color.secondaryContainerDark
import com.vocabri.ui.theme.color.secondaryContainerDarkHighContrast
import com.vocabri.ui.theme.color.secondaryContainerDarkMediumContrast
import com.vocabri.ui.theme.color.secondaryContainerLight
import com.vocabri.ui.theme.color.secondaryContainerLightHighContrast
import com.vocabri.ui.theme.color.secondaryContainerLightMediumContrast
import com.vocabri.ui.theme.color.secondaryDark
import com.vocabri.ui.theme.color.secondaryDarkHighContrast
import com.vocabri.ui.theme.color.secondaryDarkMediumContrast
import com.vocabri.ui.theme.color.secondaryLight
import com.vocabri.ui.theme.color.secondaryLightHighContrast
import com.vocabri.ui.theme.color.secondaryLightMediumContrast
import com.vocabri.ui.theme.color.surfaceBrightDark
import com.vocabri.ui.theme.color.surfaceBrightDarkHighContrast
import com.vocabri.ui.theme.color.surfaceBrightDarkMediumContrast
import com.vocabri.ui.theme.color.surfaceBrightLight
import com.vocabri.ui.theme.color.surfaceBrightLightHighContrast
import com.vocabri.ui.theme.color.surfaceBrightLightMediumContrast
import com.vocabri.ui.theme.color.surfaceContainerDark
import com.vocabri.ui.theme.color.surfaceContainerDarkHighContrast
import com.vocabri.ui.theme.color.surfaceContainerDarkMediumContrast
import com.vocabri.ui.theme.color.surfaceContainerHighDark
import com.vocabri.ui.theme.color.surfaceContainerHighDarkHighContrast
import com.vocabri.ui.theme.color.surfaceContainerHighDarkMediumContrast
import com.vocabri.ui.theme.color.surfaceContainerHighLight
import com.vocabri.ui.theme.color.surfaceContainerHighLightHighContrast
import com.vocabri.ui.theme.color.surfaceContainerHighLightMediumContrast
import com.vocabri.ui.theme.color.surfaceContainerHighestDark
import com.vocabri.ui.theme.color.surfaceContainerHighestDarkHighContrast
import com.vocabri.ui.theme.color.surfaceContainerHighestDarkMediumContrast
import com.vocabri.ui.theme.color.surfaceContainerHighestLight
import com.vocabri.ui.theme.color.surfaceContainerHighestLightHighContrast
import com.vocabri.ui.theme.color.surfaceContainerHighestLightMediumContrast
import com.vocabri.ui.theme.color.surfaceContainerLight
import com.vocabri.ui.theme.color.surfaceContainerLightHighContrast
import com.vocabri.ui.theme.color.surfaceContainerLightMediumContrast
import com.vocabri.ui.theme.color.surfaceContainerLowDark
import com.vocabri.ui.theme.color.surfaceContainerLowDarkHighContrast
import com.vocabri.ui.theme.color.surfaceContainerLowDarkMediumContrast
import com.vocabri.ui.theme.color.surfaceContainerLowLight
import com.vocabri.ui.theme.color.surfaceContainerLowLightHighContrast
import com.vocabri.ui.theme.color.surfaceContainerLowLightMediumContrast
import com.vocabri.ui.theme.color.surfaceContainerLowestDark
import com.vocabri.ui.theme.color.surfaceContainerLowestDarkHighContrast
import com.vocabri.ui.theme.color.surfaceContainerLowestDarkMediumContrast
import com.vocabri.ui.theme.color.surfaceContainerLowestLight
import com.vocabri.ui.theme.color.surfaceContainerLowestLightHighContrast
import com.vocabri.ui.theme.color.surfaceContainerLowestLightMediumContrast
import com.vocabri.ui.theme.color.surfaceDark
import com.vocabri.ui.theme.color.surfaceDarkHighContrast
import com.vocabri.ui.theme.color.surfaceDarkMediumContrast
import com.vocabri.ui.theme.color.surfaceDimDark
import com.vocabri.ui.theme.color.surfaceDimDarkHighContrast
import com.vocabri.ui.theme.color.surfaceDimDarkMediumContrast
import com.vocabri.ui.theme.color.surfaceDimLight
import com.vocabri.ui.theme.color.surfaceDimLightHighContrast
import com.vocabri.ui.theme.color.surfaceDimLightMediumContrast
import com.vocabri.ui.theme.color.surfaceLight
import com.vocabri.ui.theme.color.surfaceLightHighContrast
import com.vocabri.ui.theme.color.surfaceLightMediumContrast
import com.vocabri.ui.theme.color.surfaceVariantDark
import com.vocabri.ui.theme.color.surfaceVariantDarkHighContrast
import com.vocabri.ui.theme.color.surfaceVariantDarkMediumContrast
import com.vocabri.ui.theme.color.surfaceVariantLight
import com.vocabri.ui.theme.color.surfaceVariantLightHighContrast
import com.vocabri.ui.theme.color.surfaceVariantLightMediumContrast
import com.vocabri.ui.theme.color.tertiaryContainerDark
import com.vocabri.ui.theme.color.tertiaryContainerDarkHighContrast
import com.vocabri.ui.theme.color.tertiaryContainerDarkMediumContrast
import com.vocabri.ui.theme.color.tertiaryContainerLight
import com.vocabri.ui.theme.color.tertiaryContainerLightHighContrast
import com.vocabri.ui.theme.color.tertiaryContainerLightMediumContrast
import com.vocabri.ui.theme.color.tertiaryDark
import com.vocabri.ui.theme.color.tertiaryDarkHighContrast
import com.vocabri.ui.theme.color.tertiaryDarkMediumContrast
import com.vocabri.ui.theme.color.tertiaryLight
import com.vocabri.ui.theme.color.tertiaryLightHighContrast
import com.vocabri.ui.theme.color.tertiaryLightMediumContrast

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

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(val color: Color, val onColor: Color, val colorContainer: Color, val onColorContainer: Color)

val unspecified_scheme = ColorFamily(
    Color.Unspecified,
    Color.Unspecified,
    Color.Unspecified,
    Color.Unspecified,
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
