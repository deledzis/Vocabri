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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vocabri.logger.logger
import com.vocabri.ui.addword.AddWordScreen
import com.vocabri.ui.dictionary.DictionaryScreen
import com.vocabri.ui.dictionarydetails.DictionaryDetailsScreen
import com.vocabri.ui.settings.SettingsScreen
import com.vocabri.ui.training.TrainingScreen

val log = logger("AppNavigation")

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavigationRoute.Dictionary.destination) {
        composable(NavigationRoute.Dictionary.destination) {
            LaunchedEffect(navController.currentDestination) {
                log.i { "Navigating to DictionaryScreen" }
            }
            DictionaryScreen(navController = navController)
        }
        composable(
            NavigationRoute.DictionaryDetails.destination,
            arguments = listOf(navArgument("groupType") { type = NavType.StringType }),
        ) { backStackEntry ->
            val wordGroup = backStackEntry.arguments?.getString("groupType") ?: ""
            LaunchedEffect(navController.currentDestination) {
                log.i { "Navigating to DictionaryDetailsScreen for group $wordGroup" }
            }
            DictionaryDetailsScreen(navController = navController, wordGroup = wordGroup)
        }
        composable(NavigationRoute.Training.destination) {
            LaunchedEffect(navController.currentDestination) {
                log.i { "Navigating to TrainingScreen" }
            }
            TrainingScreen()
        }
        composable(NavigationRoute.Settings.destination) {
            LaunchedEffect(navController.currentDestination) {
                log.i { "Navigating to SettingsScreen" }
            }
            SettingsScreen()
        }
        composable(NavigationRoute.AddWord.destination) {
            LaunchedEffect(navController.currentDestination) {
                log.i { "Navigating to AddWordScreen" }
            }
            AddWordScreen(navController = navController)
        }
    }
}
