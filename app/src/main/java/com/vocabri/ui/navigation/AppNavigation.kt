package com.vocabri.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vocabri.ui.addword.AddWordScreen
import com.vocabri.ui.dictionary.DictionaryScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "dictionary") {
        composable("dictionary") {
            DictionaryScreen(navController = navController)
        }
        composable("addWord") {
            AddWordScreen(navController = navController)
        }
    }
}
