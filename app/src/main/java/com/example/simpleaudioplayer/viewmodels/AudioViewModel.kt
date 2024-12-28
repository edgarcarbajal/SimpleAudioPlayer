package com.example.simpleaudioplayer.viewmodels

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.simpleaudioplayer.models.Audio
import com.example.simpleaudioplayer.services.AudioServiceHandler
import com.example.simpleaudioplayer.services.AudioState
import com.example.simpleaudioplayer.services.PlayerEvent
import com.example.simpleaudioplayer.services.localdata.AudioStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private val audioDefaultDummy = Audio(
    "".toUri(),
    0L,
    "",
    "",
    0,
    "",
    "",
    "",
    "",
)

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val audioServiceHandler: AudioServiceHandler,
    private val databaseRepo: AudioStorage,
    savedStateHandle: SavedStateHandle, // Used to update states between UI and logic that lives in the AudioServices
):ViewModel() {
    // Similar to the @State & @Observable behavior in iOS dev? where updates/changes on any of the saveable states causes a refresh in UI
    var duration by savedStateHandle.saveable{ mutableLongStateOf(0L) } // Time of Audio in ms
    var progress by savedStateHandle.saveable{ mutableFloatStateOf(0f) } // progress of audio in percentage(aka: bar) format
    var progressString by savedStateHandle.saveable{ mutableStateOf("00:00") } // progress of audio in string format
    var isPlaying by savedStateHandle.saveable{ mutableStateOf(false) }     // song playing state
    var currentSelectedAudio by savedStateHandle.saveable{ mutableStateOf(audioDefaultDummy) }  // currently selected song in UI
    var audioList by savedStateHandle.saveable{ mutableStateOf(listOf<Audio>()) }   // List of songs displayed to user

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Initial)
    var uiState: StateFlow<UIState> = _uiState // exposed to other classes (this is the immutable ver. so other services cannot edit it)

    //init the data we will use for viewmodel
    init {
        loadAudioData()
    }

    // init the views that will use this view model! (ie: set the info to the state vars in savedState?
    init {
        viewModelScope.launch {
            audioServiceHandler.audioState.collectLatest { mediaState ->
                when(mediaState) {
                    AudioState.Initial -> _uiState.value = UIState.Initial
                    is AudioState.Buffering -> calcAudioProgressValue(mediaState.progress)
                    is AudioState.Playing -> isPlaying = mediaState.isPlaying
                    is AudioState.Progress -> calcAudioProgressValue(mediaState.progress)
                    is AudioState.CurrentlyPlaying ->  currentSelectedAudio = audioList[mediaState.mediaItemIndex]
                    is AudioState.Ready -> {
                        duration = mediaState.duration
                        _uiState.value = UIState.Ready
                    }
                }
            }
        }
    }

    // Stop the Player from playing Audio once app/VM is closed?
    override fun onCleared() {
        viewModelScope.launch {
            audioServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        }
        super.onCleared()
    }

    private fun loadAudioData() {
        viewModelScope.launch {
            val audio = databaseRepo.getAudioData()
            audioList = audio // set audio List before using setMediaItems to map the list into media items

            setMediaItems()
        }
    }

    // convert audioList into MediaItem list so that Media3 Library can use song/audio info (cannot currently use our Audio.kt Model to interact with library)
    private fun setMediaItems() {
        audioList.map {audio ->
            MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audio.artist)
                        .setDisplayTitle(audio.title)
                        .setSubtitle(audio.displayName)
                        .build()
                )
                .build()
        }.also {// once done, grab newly converted list and set the state for the handler!
            audioServiceHandler.setMediaItemList(it)
        }
    }


    // functions used in class for init & other stuff to update UI

    // calculate the current progress of song (in percentage value)
    private fun calcAudioProgressValue(currProgress: Long) {
        progress = if(currProgress > 0)
                        ((currProgress.toFloat() / duration.toFloat()) * 100f)
                    else
                        0f

        progressString = formatDurationStr(currProgress)
    }

    fun formatDurationStr(currDuration: Long): String {
        // Duration is in form of ms, so convert to minutes (return val is a Long, so will lose some precision(ie: seconds) here!)
        val minutes = TimeUnit.MINUTES.convert(currDuration, TimeUnit.MILLISECONDS)

        // Get the missing precision from our minutes conversion by converting back to ms, then subtracting new conversion with original duration
        val truncatedMilli = currDuration - TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES)

        // use leftover to calculate seconds
        val seconds = TimeUnit.SECONDS.convert(truncatedMilli, TimeUnit.SECONDS)
            //minutes - (TimeUnit.SECONDS.convert(1, TimeUnit.SECONDS)) //<-- how it was written in video, but I did not understand how this would get us the right minutes (unless its due to overflow/underflow of long?)

        return String.format("%02d:%02d", minutes, seconds)
    }

    // These "viewModelScope" coroutines will launch when we have JetPack compose launch them! (ie: the UI/ View)
    fun onUIEvents(uiEvents: UIEvents) = viewModelScope.launch {
        when(uiEvents) {
            UIEvents.Backwards -> audioServiceHandler.onPlayerEvents(PlayerEvent.Backward)
            UIEvents.Forwards -> audioServiceHandler.onPlayerEvents(PlayerEvent.Forward)
            is UIEvents.PlayPause -> audioServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
            UIEvents.Stop -> audioServiceHandler.onPlayerEvents(PlayerEvent.Stop)
            UIEvents.SeekToNext -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext)
            UIEvents.SeekToPrevious -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToPrevious)
            is UIEvents.SeekTo -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekTo, seekPosition = ((duration * uiEvents.position) / 100f).toLong())
            is UIEvents.SelectedAudioChange -> audioServiceHandler.onPlayerEvents(PlayerEvent.SelectedAudioChange, selectedAudioIndex = uiEvents.index)
            is UIEvents.UpdateProgress -> {
                // Update progress on Player
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.UpdateProgress(uiEvents.newProgress)
                )
                // Update progress in ViewModel (so that it shows up in UI)
                progress = uiEvents.newProgress
            }
        }
    }
}


// similar or same events that happen in AudioServiceHandler (PlayerEvents)
sealed class UIEvents {
    object PlayPause: UIEvents()
    data class SelectedAudioChange(val index: Int): UIEvents()
    data class SeekTo(val position: Float): UIEvents()
    object SeekToNext: UIEvents()
    object SeekToPrevious: UIEvents()
    object Backwards: UIEvents()
    object Forwards: UIEvents()
    data class UpdateProgress(val newProgress: Float): UIEvents()
    object Stop: UIEvents()
}

sealed class UIState {
    object Initial: UIState()
    object Ready: UIState()
}