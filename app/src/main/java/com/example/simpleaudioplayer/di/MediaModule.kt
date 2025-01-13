package com.example.simpleaudioplayer.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.example.simpleaudioplayer.services.AudioNotificationManager
import com.example.simpleaudioplayer.services.AudioServiceHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



// Object entirely for the auto-Dependency Injection(di) used in hilt
// Makes it so that Hilt & Dagger library can auto DI the classes/services made in this app to where they are needed
@Module
@InstallIn(SingletonComponent::class)
object MediaModule {
    @Provides
    @Singleton
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @Provides
    @Singleton
    @UnstableApi
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes,
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true) // for when people call when playing audio
        .setTrackSelector(DefaultTrackSelector(context))
        .setWakeMode(C.WAKE_MODE_LOCAL) // Makes it so foreground music playback stays on when exiting (not killing) the app
        .build()


    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): MediaSession = MediaSession.Builder(context, player).build()


    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): AudioNotificationManager = AudioNotificationManager(context, player)


    @Provides
    @Singleton
    fun provideServiceHandler(player: ExoPlayer): AudioServiceHandler = AudioServiceHandler(player)
}