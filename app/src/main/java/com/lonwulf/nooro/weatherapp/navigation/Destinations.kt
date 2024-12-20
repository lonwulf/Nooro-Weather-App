package com.lonwulf.nooro.weatherapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector


object DestinationConstants {
    const val HOME_SCREEN = "Home"
    const val CACHED_WEATHER_SCREEN = "History"
    const val SETTINGS_SCREEN = "Settings"
    const val SEARCH_RESULT_SCREEN = "Search"
}

sealed class TopLevelDestinations(val route: String, val icon: ImageVector, val title: String) {
    object HomeScreen :
        TopLevelDestinations(DestinationConstants.HOME_SCREEN, Icons.Outlined.Home, "Home")

    object HistoryScreen : TopLevelDestinations(
        DestinationConstants.CACHED_WEATHER_SCREEN,
        Icons.Outlined.FavoriteBorder,
        "History"
    )

    object SettingsScreen :
        TopLevelDestinations(
            DestinationConstants.SETTINGS_SCREEN,
            Icons.Outlined.Settings,
            "Settings"
        )

}

sealed class Destinations(val route: String) {
    object SearchScreen : Destinations(DestinationConstants.SEARCH_RESULT_SCREEN)
}
