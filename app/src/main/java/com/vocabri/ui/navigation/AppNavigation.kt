package com.vocabri.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vocabri.domain.util.logger
import com.vocabri.ui.addword.AddWordScreen
import com.vocabri.ui.dictionary.DictionaryScreen

val log = logger("AppNavigation")

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "dictionary") {
        composable("dictionary") {
            LaunchedEffect(navController.currentDestination) {
                log.i { "Navigating to DictionaryScreen" }
            }
            DictionaryScreen(navController = navController)
        }
        composable("addWord") {
            LaunchedEffect(navController.currentDestination) {
                log.i { "Navigating to AddWordScreen" }
            }
            AddWordScreen(navController = navController)
        }
    }
}
