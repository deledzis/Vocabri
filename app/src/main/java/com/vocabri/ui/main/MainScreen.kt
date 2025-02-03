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
package com.vocabri.ui.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vocabri.di.qualifiers.DictionaryDetailsQualifiers
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.logger.logger
import com.vocabri.ui.main.NavigationRoutes.bottomNavigationScreens
import com.vocabri.ui.navigation.AppNavigation
import com.vocabri.ui.navigation.MainBottomNavigation
import com.vocabri.ui.navigation.NavigationRoute
import com.vocabri.ui.screens.addword.components.AddWordScreenTopAppBar
import com.vocabri.ui.screens.dictionary.components.DictionaryScreenTopAppBar
import com.vocabri.ui.screens.dictionarydetails.components.DictionaryDetailsScreenTopAppBar
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsViewModel
import com.vocabri.ui.screens.discovermore.components.DiscoverMoreScreenTopAppBar
import com.vocabri.ui.screens.settings.components.SettingsScreenTopAppBar
import com.vocabri.ui.screens.training.components.TrainingScreenTopAppBar
import org.koin.androidx.compose.koinViewModel

object NavigationRoutes {
    val bottomNavigationScreens = listOf(
        NavigationRoute.Start.Dictionary,
        NavigationRoute.Start.Training,
        NavigationRoute.Empty,
        NavigationRoute.Start.DiscoverMore,
        NavigationRoute.Start.Settings,
    )
}

val log = logger("MainScreen")

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val focusManager = LocalFocusManager.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    Scaffold(
        modifier = modifier,
        topBar = {
            log.i { "CURRENT ROUTE $currentRoute" }
            when (currentRoute) {
                NavigationRoute.Start.Dictionary.route -> DictionaryScreenTopAppBar()
                NavigationRoute.Start.Training.route -> TrainingScreenTopAppBar()
                NavigationRoute.Start.DiscoverMore.route -> DiscoverMoreScreenTopAppBar()
                NavigationRoute.Start.Settings.route -> SettingsScreenTopAppBar()
                NavigationRoute.Secondary.AddWord.route -> {
                    AddWordScreenTopAppBar(
                        focusManager = focusManager,
                        navController = navController,
                    )
                }

                NavigationRoute.Secondary.DictionaryDetails.route -> {
                    val partOfSpeech = navBackStackEntry!!.arguments?.getString("partOfSpeech").orEmpty()
                    val viewModel: DictionaryDetailsViewModel = koinViewModel(
                        qualifier = DictionaryDetailsQualifiers.fromPartOfSpeech(PartOfSpeech.valueOf(partOfSpeech)),
                        viewModelStoreOwner = navBackStackEntry!!,
                    )
                    val uiState by viewModel.state.collectAsState()
                    DictionaryDetailsScreenTopAppBar(state = uiState, navController = navController)
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = currentRoute in bottomNavigationScreens.map { it.route },
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                MainBottomNavigation(
                    navController = navController,
                    navigationRoutes = bottomNavigationScreens,
                ) {
                    if (navController.currentDestination?.route != NavigationRoute.Secondary.AddWord.route) {
                        navController.navigate(NavigationRoute.Secondary.AddWord.route) {
                            launchSingleTop = true
                        }
                    }
                }
            }
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavigation(navController = navController, focusManager = focusManager)
        }
    }
}
