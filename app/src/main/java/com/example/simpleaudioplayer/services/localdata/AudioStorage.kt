package com.example.simpleaudioplayer.services.localdata

import com.example.simpleaudioplayer.models.Audio
import com.example.simpleaudioplayer.services.localdata.AudioContentRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


// Injecting using "@notations" from Hilt framework - Allows for auto dependency Injection - No manual writing bolierplate
// to do so yourself
class AudioStorage @Inject constructor(
    private val contentRetriever: AudioContentRetriever
){
    suspend fun getAudioData(): List<Audio> = withContext(Dispatchers.IO) {
        contentRetriever.getAudioData()
    }
}