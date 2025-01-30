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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.vocabri.R

sealed interface NavigationRoute {
    val route: String

    data object Empty : NavigationRoute {
        override val route: String = "empty"
    }

    sealed class Start(
        override val route: String,
        @StringRes val titleResId: Int,
        @DrawableRes val iconSelectedResId: Int,
        @DrawableRes val iconUnselectedResId: Int,
    ) : NavigationRoute {

        data object Dictionary : Start(
            route = "dictionary",
            titleResId = R.string.navigation_screen_title_dictionary,
            iconSelectedResId = R.drawable.ic_navigation_dictionary,
            iconUnselectedResId = R.drawable.ic_navigation_dictionary,
        )

        data object Training : Start(
            route = "training",
            titleResId = R.string.navigation_screen_title_exercise,
            iconSelectedResId = R.drawable.ic_navigation_exercise,
            iconUnselectedResId = R.drawable.ic_navigation_exercise,
        )

        data object DiscoverMore : Start(
            route = "discover_more",
            titleResId = R.string.navigation_screen_title_discover_more,
            iconSelectedResId = R.drawable.ic_navigation_discover_more,
            iconUnselectedResId = R.drawable.ic_navigation_discover_more,
        )

        data object Settings : Start(
            route = "settings",
            titleResId = R.string.navigation_screen_title_settings,
            iconSelectedResId = R.drawable.ic_navigation_settings,
            iconUnselectedResId = R.drawable.ic_navigation_settings,
        )
    }

    sealed class Secondary(override val route: String) : NavigationRoute {
        data object DictionaryDetails : Secondary(
            route = "group_details/{groupType}",
        ) {
            fun fromGroup(groupType: String) =
                DictionaryDetails.route.replace("{groupType}", groupType)
        }

        data object AddWord : Secondary(
            route = "add_word",
        )
    }
}
