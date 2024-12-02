package com.example.simpleaudioplayer.services

import android.content.Intent
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService


// Most of the content here in from Google & Medium article, with some modifications to make it work from this app.
// Google Article: https://developer.android.com/media/implement/playback-app
// Medium Article: https://medium.com/@ouzhaneki/basic-background-playback-implementation-with-media3-mediasessionservice-4d571f15bdc2

// Need to declare this as a Service in AndroidManifest.xml since we inherit from an already declared service!
class AudioPlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    /**
     * This method is called when the service is being created.
     * It initializes the ExoPlayer and MediaSession instances.
     */
    override fun onCreate() {
        super.onCreate() // Call the superclass method (ie: the onCreate from the parent class)

        // Create an ExoPlayer instance - with Audio-Focused behaviors enabled
        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()

        // Create a MediaSession instance
        mediaSession = MediaSession.Builder(this, player).build()

        Log.d("AudioPlayerService-Test-[mediaSession]", mediaSession.toString())
    }

    /**
     * This method is called when the system determines that the service is no longer used and is being removed.
     * It checks the player's state and if the player is not ready to play or there are no items in the media queue, it stops the service.
     *
     * @param rootIntent The original root Intent that was used to launch the task that is being removed.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        mediaSession?.run {
            // Check if the player is not ready to play or there are no items in the media queue
            if (!player.playWhenReady || player.mediaItemCount == 0) {
                // Stop the service
                stopSelf()
            }
        }
    }

    /**
     * This method is called when a MediaSession.ControllerInfo requests the MediaSession connection.
     * It returns the current MediaSession instance. Returns `null` if want to reject a connection.
     *
     * @param controllerInfo The MediaSession. ControllerInfo that is requesting the MediaSession connection.
     * @return The current MediaSession instance. If null, connection was rejected
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.d("AudioPlayerService-Test", "Session-Get requested!!")
        return mediaSession
    }

    /**
     * This method is called when the service is being destroyed.
     * It releases the player and the MediaSession instances.
     */
    override fun onDestroy() {
        // If mediaSession is not null, run the following block
        mediaSession?.run {
            // Release the player
            player.release()

            // Release the MediaSession instance
            release()

            // revert mediaSession back to being null
            mediaSession = null
        }
        // Call the superclass method
        super.onDestroy()
    }
}