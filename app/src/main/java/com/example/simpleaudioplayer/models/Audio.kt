package com.example.simpleaudioplayer.models

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class Audio(
    val uri: Uri,
    val id: Long,
    val displayName: String,
    val data: String,
    val duration: Int,
    val title: String,
    val album: String,
    val albumArt: ImageBitmap?,
    val artist: String,
    val genre: String,
)
