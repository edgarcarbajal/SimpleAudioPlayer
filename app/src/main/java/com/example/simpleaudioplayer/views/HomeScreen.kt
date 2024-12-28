package com.example.simpleaudioplayer.views


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.simpleaudioplayer.viewmodels.AudioViewModel
import com.example.simpleaudioplayer.viewmodels.UIEvents

@Composable
fun HomeScreen(
    startService: () -> Unit,
    navController: NavController,
    audioVM: AudioViewModel,
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BasicAudioHome(
            progress = audioVM.progress,
            onProgress = { audioVM.onUIEvents(UIEvents.SeekTo(it)) },
            isAudioPlaying = audioVM.isPlaying,
            currentAudio = audioVM.currentSelectedAudio,
            audioList = audioVM.audioList,
            onStart = { audioVM.onUIEvents(UIEvents.PlayPause) },
            onItemClick = {
                audioVM.onUIEvents(UIEvents.SelectedAudioChange(it))
                startService()
            },
            onNext = { audioVM.onUIEvents(UIEvents.SeekToNext) },
            onPrevious = { audioVM.onUIEvents(UIEvents.SeekToPrevious) },
        )
    }
}

// Cannot accurately preview when ViewModels are dependencies??? Or at least what docs say
//showSystemUi = true
//@Preview(showBackground = true)
//@Composable
//fun HomeScreen_Preview() {
//    HomeScreen(rememberNavController())
//}