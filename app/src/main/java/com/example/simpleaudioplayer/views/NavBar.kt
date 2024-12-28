package com.example.simpleaudioplayer.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpleaudioplayer.viewmodels.AudioViewModel
import com.example.simpleaudioplayer.viewmodels.NaviScreens
import com.example.simpleaudioplayer.viewmodels.NavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    startAudioService: () -> Unit,
) {
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
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Simple Audio Player")
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                tonalElevation = 8.dp
            ) {
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
            composable(NaviScreens.Home.route) {backStackEntry ->
                val audioParentVM = hiltViewModel<AudioViewModel>()
                HomeScreen(
                    startAudioService,
                    navController = navController,
                    audioParentVM
                )
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

@Preview(showBackground = true)
@Composable
fun BottomNavBar_Preview() {
    BottomNavBar(
        startAudioService = {}
    )
}