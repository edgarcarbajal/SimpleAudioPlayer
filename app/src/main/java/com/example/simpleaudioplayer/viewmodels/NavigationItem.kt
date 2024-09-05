package com.example.simpleaudioplayer.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// navigation items used for bottom navigation bar - includes a function to generate the necessary ones!
data class NavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {
    fun getNavBarItems() : List<NavigationItem> {
        return listOf(
            NavigationItem(
                "Home",
                Icons.Filled.Home,
                "home_R"
            ),
            NavigationItem(
                "Search",
                Icons.Filled.Search,
                "search_R"
            ),
            NavigationItem(
                "Settings",
                Icons.Filled.Settings,
                "settings_R"
            )
        )
    }
}
