package com.example.simpleaudioplayer.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.simpleaudioplayer.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject



/*
    Creates & manages the notification that shows the music player
*/
private const val NOTIFICATION_ID = 0xED1
private const val NOTIFICATION_CHANNEL_NAME = "Notification Channel 0xED1(3793)"
private const val NOTIFICATION_CHANNEL_ID = "Notification Channel ID: 0xED1(3793)"
class AudioNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context, // needed to manage OS specific tasks, like notifications
    private val exoPlayer: ExoPlayer,
){
    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel() // ie: call this func when constructing object for first time
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW,
        )

        notificationManager.createNotificationChannel(channel)
    }

    // Will be called in AudioService - Allows for the service to interact with the notification(that will have an audio player UI)
    @UnstableApi
    fun startNotificationService( // Background Version! - creates notif and runs in background
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ) {
        buildPlayerNotification(mediaSession)
        startForegroundNotificationService(mediaSessionService)
    }

    private fun startForegroundNotificationService(mediaSessionService: MediaSessionService) { // foreground ver. Allows for use of the notification when user is interacting with UI
        val notification = Notification.Builder(
            context,
            NOTIFICATION_CHANNEL_ID
        )
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    @UnstableApi
    private fun buildPlayerNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID,
        )
            .setMediaDescriptionAdapter(
                AudioNotificationAdapter(
                    context = context,
                    pendingIntent = mediaSession.sessionActivity
                )
            ) // converts info into format that fits with Notifications??
            .setSmallIconResourceId(R.drawable.ic_launcher_foreground) // icon that shows up in notification badge
            .build()
            .also {// run this code after done building notification - notification customization options
                it.setMediaSessionToken(mediaSession.platformToken)
                it.setPriority(NotificationCompat.PRIORITY_LOW)

                it.setUseFastForwardAction(true)
                it.setUseRewindAction(true)
                it.setUsePlayPauseActions(true)
                it.setUseNextAction(true)
                it.setUsePreviousAction(true)
                it.setUseNextActionInCompactView(true)
                it.setUsePreviousActionInCompactView(true)
                it.setColorized(true)

                it.setPlayer(exoPlayer) // attach class that manages UI experience to the notif
            }
    }
}