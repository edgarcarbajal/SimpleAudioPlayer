package com.example.simpleaudioplayer

import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.simpleaudioplayer.services.AudioPlayerService
import com.example.simpleaudioplayer.ui.theme.SimpleAudioPlayerTheme
import com.example.simpleaudioplayer.views.BottomNavBar
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class MainActivity : ComponentActivity() {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var playerView: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Init. the MediaController UI - Use to connect to AudioPlayerService to play audio
        val sessionToken = SessionToken(this, ComponentName(this, AudioPlayerService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken)
            .buildAsync()

        controllerFuture?.addListener({
            playerView = controllerFuture?.get()
        }, MoreExecutors.directExecutor())


        Log.d("MediaController-test", "Hello. Below is the playerview/mediacontroller:")
        Log.d("MediaController-test", playerView.toString())


        // Insert views/composables here
        setContent {
            SimpleAudioPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BottomNavBar()
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()

        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        controllerFuture = null

        playerView = null
    }
}
