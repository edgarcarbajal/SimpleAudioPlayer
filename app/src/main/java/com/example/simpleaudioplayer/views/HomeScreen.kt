package com.example.simpleaudioplayer.views


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(navController: NavController) {
    NavigationBar(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BasicAudioHome(
            progress = 0.3f,
            onProgress = { /*TODO*/ },
            isAudioPlaying = true,
            currentAudio = dummyAudio2,
            audioList = listOf(dummyAudio, dummyAudio2),
            onStart = { /*TODO*/ },
            onItemClick = {},
            onNext = { /*TODO*/ }) {}
    }
}

//showSystemUi = true
@Preview(showBackground = true)
@Composable
fun HomeScreen_Preview() {
    HomeScreen(rememberNavController())
}