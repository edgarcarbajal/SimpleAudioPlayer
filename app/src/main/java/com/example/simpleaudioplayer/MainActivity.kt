package com.example.simpleaudioplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import com.example.simpleaudioplayer.services.AudioService
import com.example.simpleaudioplayer.ui.theme.SimpleAudioPlayerTheme
import com.example.simpleaudioplayer.viewmodels.AudioViewModel
import com.example.simpleaudioplayer.views.BottomNavBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint // Needed to insert the DI classes/services we made for AudioPlayer using Hilt
class MainActivity : ComponentActivity() {
    //private val audioVM: AudioViewModel by viewModels()
    private var isServiceRunning = false
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Insert views/composables here
        setContent {
            SimpleAudioPlayerTheme {
                val permissionState = rememberPermissionState( // Keep whether or not user gave access to read audio files
                    permission = android.Manifest.permission.READ_MEDIA_AUDIO
                )
                // Code below checks when app is running(specifically when MainActivity is first created) to create the dialog to ask user for Permissions
                // Only runs once, then removed per MainActivity View creation(ie: everytime app boots up/not in memory?)
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(key1 = lifecycleOwner){
                    val observer = LifecycleEventObserver{_, event ->
                        if(event == Lifecycle.Event.ON_RESUME) {
                            permissionState.launchPermissionRequest()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer = observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }


                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BottomNavBar({startAudioService()})
                }
            }
        }
    }

    private fun startAudioService() {
        if(!isServiceRunning) {
            val intent = Intent(this, AudioService::class.java)
            startForegroundService(intent)
        }
        else {
            startService(intent)
        }

        isServiceRunning = true
    }
}
