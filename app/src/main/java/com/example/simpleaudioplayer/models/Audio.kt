package com.example.simpleaudioplayer.models

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.net.toUri

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
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader) ?: "NullParsed_URI".toUri(),
        parcel.readLong(),
        parcel.readString() ?: "NullParsed_String",
        parcel.readString() ?: "NullParsed_String",
        parcel.readInt(),
        parcel.readString() ?: "NullParsed_String",
        parcel.readString() ?: "NullParsed_String",
        parcel.readString() ?: "NullParsed_String",
        parcel.readString() ?: "NullParsed_String"
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uri, flags)
        parcel.writeLong(id)
        parcel.writeString(displayName)
        parcel.writeString(data)
        parcel.writeInt(duration)
        parcel.writeString(title)
        parcel.writeString(album)
        parcel.writeString(artist)
        parcel.writeString(genre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Audio> {
        override fun createFromParcel(parcel: Parcel): Audio {
            return Audio(parcel)
        }

        override fun newArray(size: Int): Array<Audio?> {
            return arrayOfNulls(size)
        }
    }
}
