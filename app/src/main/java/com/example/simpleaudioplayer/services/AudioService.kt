package com.example.simpleaudioplayer.services

import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
    Creates MediaSessionService for app - allows for the background interaction between the player/UI and other services like media
    browser, background playback, or other services/apps
*/

// Hilt- Auto Dependency Inj. for the inherited class of MediaSessionService
// basically allows for injection of parameters using Hilt
@AndroidEntryPoint
class AudioService:MediaSessionService() {

    @Inject
    lateinit var mediaSession: MediaSession

    @Inject
    lateinit var notificationManager: AudioNotificationManager

    // Override this parent func to allow to start our notification when playing a song
    @UnstableApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager.startNotificationService(
            mediaSessionService = this,
            mediaSession
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession.player
        // If exiting app, check if player has no more songs in queue -
        // If no more, then stop player, else keep playing music (ie: acts like background music play)
        if(!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    // Same as saying "return mediaSession" in code!
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession
    override fun onDestroy() {
        mediaSession.apply {
            release() // remove any connections to given session
            if(player.playbackState != Player.STATE_IDLE) { // stops music playing if there was any - resets pos to 0
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
            player.release()
        }

        super.onDestroy()
    }
}