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
package com.vocabri.notifications.navigation

import androidx.navigation.NavHostController
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.notifications.deeplink.DeeplinkData
import com.vocabri.ui.navigation.NavigationRoute

/**
 * Maps notification deeplinks into concrete navigation destinations.
 */
class NotificationDeeplinkNavigator {
    fun navigate(navController: NavHostController, deeplink: DeeplinkData) {
        val destination = resolveDestination(deeplink)
        navController.navigate(destination) {
            launchSingleTop = true
        }
    }

    private fun resolveDestination(deeplink: DeeplinkData): String {
        val normalizedRoute = deeplink.route.lowercase()
        return when {
            normalizedRoute.startsWith("dict/") -> {
                resolveDictionaryDetails(deeplink)
            }
            normalizedRoute == NavigationRoute.Start.Dictionary.route -> NavigationRoute.Start.Dictionary.route
            normalizedRoute == NavigationRoute.Start.Training.route -> NavigationRoute.Start.Training.route
            normalizedRoute == "discover" || normalizedRoute == NavigationRoute.Start.DiscoverMore.route -> NavigationRoute.Start.DiscoverMore.route
            normalizedRoute == NavigationRoute.Start.Settings.route -> NavigationRoute.Start.Settings.route
            normalizedRoute == NavigationRoute.Secondary.AddWord.route -> NavigationRoute.Secondary.AddWord.route
            else -> NavigationRoute.Start.Dictionary.route
        }
    }

    private fun resolveDictionaryDetails(deeplink: DeeplinkData): String {
        val partOfSpeechSegment = deeplink.pathSegments.getOrNull(1)
            ?: deeplink.arguments[ARG_PART_OF_SPEECH]
            ?: return NavigationRoute.Start.Dictionary.route
        val normalized = partOfSpeechSegment.uppercase()
        val partOfSpeech = PartOfSpeech.entries.find { it.name == normalized }
            ?: return NavigationRoute.Start.Dictionary.route
        return NavigationRoute.Secondary.DictionaryDetails.fromGroup(partOfSpeech.name)
    }

    companion object {
        private const val ARG_PART_OF_SPEECH = "partOfSpeech"
    }
}
