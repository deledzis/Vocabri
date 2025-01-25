package com.vocabri.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vocabri.domain.util.logger
import com.vocabri.ui.addword.AddWordScreen
import com.vocabri.ui.dictionary.DictionaryScreen
import com.vocabri.ui.settings.SettingsScreen
import com.vocabri.ui.training.TrainingScreen

val log = logger("AppNavigation")

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "dictionary") {
        composable(NavigationRoute.Dictionary.destination) {
            LaunchedEffect(navController.currentDestination) {
                log.i { "Navigating to DictionaryScreen" }
            }
            DictionaryScreen(navController = navController)
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
