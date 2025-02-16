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
package com.vocabri.ui.navigation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.vocabri.R
import com.vocabri.ui.components.Buttons
import com.vocabri.ui.theme.VocabriTheme

@Composable
fun MainBottomNavigation(
    modifier: Modifier = Modifier,
    navController: NavController,
    navigationRoutes: List<NavigationRoute>,
    onPlusButtonLongClick: () -> Unit,
    onPlusButtonClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.TopCenter,
    ) {
        NavigationPlate(navController = navController, navigationRoutes = navigationRoutes)
        PlusItem(
            modifier = Modifier
                .padding(top = 16.dp),
            onPlusButtonClick = onPlusButtonClick,
            onPlusButtonLongClick = onPlusButtonLongClick,
        )
    }
}

@Composable
private fun NavigationPlate(
    modifier: Modifier = Modifier,
    navController: NavController,
    navigationRoutes: List<NavigationRoute>,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        navigationRoutes.forEach { screen ->
            when (screen) {
                NavigationRoute.Empty -> EmptyBottomNavigationItem()
                is NavigationRoute.Start -> {
                    ContentBottomNavigationItem(
                        navigationRoute = screen,
                        navController = navController,
                    )
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun PlusItem(modifier: Modifier = Modifier, onPlusButtonClick: () -> Unit, onPlusButtonLongClick: () -> Unit) {
    Buttons.Filled.IconOnly(
        modifier = modifier,
        iconResId = R.drawable.ic_plus,
        contentDescriptionResId = R.string.navigation_description_plus_button,
        onLongClick = onPlusButtonLongClick,
    ) { onPlusButtonClick() }
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
private fun PreviewMainBottomNavigation() {
    VocabriTheme {
        val navController = rememberNavController()
        val bottomNavigationRoutes = listOf(
            NavigationRoute.Start.Dictionary,
            NavigationRoute.Start.Training,
            NavigationRoute.Empty,
            NavigationRoute.Start.DiscoverMore,
            NavigationRoute.Start.Settings,
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            MainBottomNavigation(
                navController = navController,
                navigationRoutes = bottomNavigationRoutes,
                modifier = Modifier.align(Alignment.Center),
                onPlusButtonLongClick = {},
            ) { }
        }
    }
}
