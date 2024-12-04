package com.example.simpleaudioplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import com.example.simpleaudioplayer.ui.theme.SimpleAudioPlayerTheme
import com.example.simpleaudioplayer.views.BottomNavBar


//<service
//android:name=".services.AudioPlayerService"
//android:foregroundServiceType="mediaPlayback"
//android:exported="true">
//<intent-filter>
//<action android:name="androidx.media3.session.MediaSessionService"/>
//</intent-filter>
//</service>
class MainActivity : ComponentActivity() {

    //private var controllerFuture: ListenableFuture<MediaController>? = null
    private lateinit var playerView: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        // Init. the MediaController UI - Use this UI to connect to AudioPlayerService to play audio
//        val sessionToken = SessionToken(this, ComponentName(this, AudioPlayerService::class.java))
//        val controllerFuture = MediaController.Builder(this, sessionToken)
//            .buildAsync()
//
//        controllerFuture.addListener({
//            playerView = controllerFuture.get()
//            playerView.play()
//        }, MoreExecutors.directExecutor())
//
//
//        Log.d("MediaController-Test", "Hello!! Testing some values!")
//        Log.d("MediaController-Test-[sessionToken]", sessionToken.toString())
//        //Log.d("MediaController-Test-[playerView]", playerView.toString())


        // Insert views/composables here
        setContent {
            SimpleAudioPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    AndroidView(factory = {context ->
//                        PlayerView(context).apply {
//                            player = this.player
//                        }
//                    })
                    BottomNavBar()
                }
            }
        }
    }
}
