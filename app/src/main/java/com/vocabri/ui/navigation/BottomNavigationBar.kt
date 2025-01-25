package com.vocabri.ui.navigation

import android.content.res.Configuration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.vocabri.R
import com.vocabri.ui.theme.VocabriTheme

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(
            route = NavigationRoute.Dictionary.destination,
            label = "Dictionary",
            iconResId = R.drawable.ic_dictionary,
        ),
        BottomNavItem(
            route = NavigationRoute.Training.destination,
            label = "Training",
            iconResId = R.drawable.ic_mindfulness,
        ),
        BottomNavItem(
            route = NavigationRoute.Settings.destination,
            label = "Settings",
            iconResId = R.drawable.ic_account,
        ),
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.label,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                selected = navController.currentDestination?.route == item.route,
                onClick = {
                    if (navController.currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
            )
        }
    }
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
@Composable
fun PreviewShimmerEffectRounded() {
    VocabriTheme {
        BottomNavigationBar(rememberNavController())
    }
}
