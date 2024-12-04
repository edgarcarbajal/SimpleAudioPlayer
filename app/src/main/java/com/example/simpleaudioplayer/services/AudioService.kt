package com.example.simpleaudioplayer.services

import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


// Hilt- Auto Dependency Inj. for the inherited class of MediaSessionService
// basically allows for injection of parameters using Hilt
@AndroidEntryPoint
class AudioService:MediaSessionService() {

    @Inject
    lateinit var mediaSession: MediaSession

    // Same as saying "return mediaSession" in code!
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession
    override fun onDestroy() {
        super.onDestroy()

        mediaSession.apply {
            release() // remove any connections to given session
            if(player.playbackState != Player.STATE_IDLE) { // stops music playing if there was any - resets pos to 0
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
        }
    }
}