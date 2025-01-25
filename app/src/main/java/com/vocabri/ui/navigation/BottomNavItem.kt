package com.vocabri.ui.navigation

import androidx.annotation.DrawableRes

data class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes
    val iconResId: Int,
)
