package com.example.simpleaudioplayer.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpleaudioplayer.viewmodels.NaviScreens
import com.example.simpleaudioplayer.viewmodels.NavigationItem

@Composable
fun BottomNavBar() {
    //initializing the default selected item - similar to react useState??
    var navbarSelectedItem by remember {
        mutableStateOf(0)
    }

    /**
     * by using the rememberNavController()
     * we can get the instance of the navController
     */
    val navController = rememberNavController()

    //Use Scaffold compose/view to create the layout on how the navbar will be displayed
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                // get navbar items
                NavigationItem().getNavBarItems().forEachIndexed { idx, navItem ->
                    // set each item given by our navbaritems to the actual navbar
                    NavigationBarItem(
                        selected = idx == navbarSelectedItem,
                        label = {
                            Text(navItem.label)
                        },
                        icon = {
                            Icon(
                                navItem.icon,
                                contentDescription = navItem.label
                            )
                        },
                        onClick = {
                            navbarSelectedItem = idx
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
        // set padding vals to for the Scaffold below & connect to navController using NavHost
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NaviScreens.Home.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(NaviScreens.Home.route) {
                HomeScreen(navController = navController)
            }

            composable(NaviScreens.Search.route) {
                SearchScreen(navController = navController)
            }

            composable(NaviScreens.Settings.route) {
                SettingsScreen(navController = navController)
            }
        }
    }
}