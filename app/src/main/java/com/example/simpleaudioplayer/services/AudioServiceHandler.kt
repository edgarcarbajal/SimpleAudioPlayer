package com.example.simpleaudioplayer.services

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/*
    Handles interactions between Player (UI) and MediaSessionService (background service that controls the actual audio player)
    Ex: User clicks play button - sends signal to MediaSessionService to use .play() method in MediaSession
        but MediaSessionService cannot directly handle that signal, thus the need for this Handler

    Also handles states of the audi player (ex: buffering, playing, pausing, etc, wiht use of sealed classes)
 */
class AudioServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer,
): Player.Listener{
    private val _audioState: MutableStateFlow<AudioState> = MutableStateFlow(AudioState.Initial) // container to manage current State - easy to change
    val audioState: StateFlow<AudioState> = _audioState.asStateFlow() // actual state needed by other services - not easy to change?

    private var job: Job? = null // background job - basically run code in background (without user interaction)

    // Used to init the Player.Listener & to get output from Player
    init {
        exoPlayer.addListener(this)
    }

    // add 1 audio track to queue
    fun addMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    // Add list of audio tracks to queue
    fun addMediaItems(mediaItems: List<MediaItem>) {
        exoPlayer.addMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    // set queue to list of audio tracks (ie: reset queue to 0, then add)
    fun setMediaItemList(mediaItems: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    // These change the state within this handler only! - This is only called by other classes/services
    suspend fun onPlayerEvents( // suspend means "async" basically!
        playerEvent: PlayerEvent,
        selectedAudioIndex: Int = -1,
        seekPosition:Long = 0,
    ) {
        when(playerEvent) {
            PlayerEvent.Backward -> exoPlayer.seekBack() // These 2 are the 10/15sec skips in UI
            PlayerEvent.Forward -> exoPlayer.seekForward()

            PlayerEvent.SeekToNext -> exoPlayer.seekToNext() // these are the Skip Track buttons
            PlayerEvent.SeekToPrevious -> exoPlayer.seekToPrevious()

            PlayerEvent.PlayPause -> togglePlayPause()

            PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition) // Moves cursor in audio track's timeline

            PlayerEvent.SelectedAudioChange -> { // when selecting a new song from list
                when(selectedAudioIndex) {
                    exoPlayer.currentMediaItemIndex -> togglePlayPause() // Acts like PlayPause button if selecting an already selected song (not sure to keep this behavior or not)

                    else -> { // Play button ONLY behavior
                        exoPlayer.seekToDefaultPosition(selectedAudioIndex)
                        _audioState.value = AudioState.Playing(true)
                        exoPlayer.playWhenReady = true
                        startAudioProgress()
                    }
                }
            }

            PlayerEvent.Stop -> stopAudioProgress()

            // Not sure what this state is for? since we have SeekTo - Maybe the UI version/connection?
            is PlayerEvent.UpdateProgress -> { // "is" keyword due to being a data class, and not an 'object'
                exoPlayer.seekTo(
                    (exoPlayer.duration * playerEvent.newProgress).toLong()
                )
            }
        }
    }

    private suspend fun togglePlayPause() {
        if(exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopAudioProgress()
        }
        else {
            exoPlayer.play()
            _audioState.value = AudioState.Playing(true)
            startAudioProgress()
        }
    }

    private suspend fun startAudioProgress() = job.run {
        while(true) {
            delay(500)
            // Basically keep in sync with ExoPlayer's progress in this Handler class
            // 0.5s should be enough time for exoPlayer's position to change in code
            _audioState.value = AudioState.Progress(exoPlayer.currentPosition)
        }
    }


    private fun stopAudioProgress() {
        job?.cancel()
        _audioState.value = AudioState.Playing(false)
    }


    // These overrides actually monitor when the state changes in ExoPlayer - They set the initial states/events in this handler
    override fun onPlaybackStateChanged(playbackState: Int) {
        when(playbackState) {
            ExoPlayer.STATE_BUFFERING -> _audioState.value = AudioState.Buffering(exoPlayer.currentPosition)
            ExoPlayer.STATE_READY -> _audioState.value = AudioState.Ready(exoPlayer.duration)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _audioState.value = AudioState.Playing(true)
        _audioState.value = AudioState.CurrentlyPlaying(exoPlayer.currentMediaItemIndex)

        if(isPlaying) { // Recommended not to use GlobalScope to run this coroutine job - Just doing it here due to simplicity/How its done in video
            GlobalScope.launch(Dispatchers.Main) {
                startAudioProgress()
            }
        }
        else {
            stopAudioProgress()
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        // Change the local state to reflect the new mediaItem/Audio that player has switched too (Should fix UI bug where Info wouldn't update after song ends, and goes to next in queue)
        _audioState.value = AudioState.Playing(true)
        _audioState.value = AudioState.CurrentlyPlaying(exoPlayer.currentMediaItemIndex)
    }
}


// Events that occur in Player - Need to keep track and implement/override behavior
sealed class PlayerEvent {
    object PlayPause: PlayerEvent()
    object SelectedAudioChange: PlayerEvent()
    object Backward: PlayerEvent()
    object Forward: PlayerEvent()
    object SeekTo: PlayerEvent()
    object SeekToNext: PlayerEvent()
    object SeekToPrevious: PlayerEvent()
    object Stop: PlayerEvent()
    data class UpdateProgress(val newProgress: Float): PlayerEvent()
}

// States that occur in mediasession - Need to keep track & implement/override
sealed class AudioState{
    object Initial:AudioState()
    data class Ready(val duration: Long): AudioState()
    data class Progress(val progress: Long): AudioState()
    data class Buffering(val progress: Long): AudioState()
    data class Playing(val isPlaying: Boolean): AudioState()
    data class CurrentlyPlaying(val mediaItemIndex: Int): AudioState()

}