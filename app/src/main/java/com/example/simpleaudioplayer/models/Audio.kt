package com.example.simpleaudioplayer.models

import android.net.Uri

data class Audio(
    val uri: Uri,
    val id: Long,
    val displayName: String,
    val data: String,
    val duration: Int,
    val title: String,
    val album: String,
    val artist: String,
    val genre: String,
)
