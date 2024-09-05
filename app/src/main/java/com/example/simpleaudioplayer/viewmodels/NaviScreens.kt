package com.example.simpleaudioplayer.viewmodels

//routes to different views/activities in app
sealed class NaviScreens(val route : String) {
    object Home : NaviScreens("home_R")
    object Search : NaviScreens("search_R")
    object Settings : NaviScreens("settings_R")
}