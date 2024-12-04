package com.example.simpleaudioplayer.services.localdata

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import com.example.simpleaudioplayer.models.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


// Code Heavily inspired by Docs & this video here by HoodLab:
// https://www.youtube.com/watch?v=XrcmjIW45u8&t=0s


// Use Android DB (ie: filesys) to get songs in local device
class AudioContentRetriever @Inject constructor(@ApplicationContext val context: Context) {
    private var mCursor: Cursor? = null // basically a reference to the DB/filesys in android? - matrix cursor? since DB is stored in table/matrix format

    private val attribSelectors: Array<String> = arrayOf(
        //MediaStore.Audio.AudioColumns.TITLE_RESOURCE_URI,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.ALBUM,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.GENRE,
    )

    private var selectionQuery: String? = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"
    private var selectionArg = arrayOf("1") // This will go in the '?' in the query above (ie: checking if IS_MUSIC column is true)

    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"


    // WorkerThread, meaning that fetching data will always happen in its own separate thread that is active??
    // To reduce data discrepancies
    @WorkerThread
    fun getAudioData(): List<Audio> {
        return getCursorData()
    }

    private fun getCursorData(): MutableList<Audio> {
        val audioList = mutableListOf<Audio>()

        mCursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            attribSelectors,
            selectionQuery,
            selectionArg,
            sortOrder
        )

        mCursor?.use { cursor ->
            // get the items/cols selected by 'attribSelectors' that have been returned thru mCursor
            val idColIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val displayNameColIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val dataColIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val durationColIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val titleColIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val albumColIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
            val artistColIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val genreColIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.GENRE)

            cursor.apply {
                if(count == 0) {
                    Log.e("mCursor", "getCursorData(): Cursor/Query results are empty! - No Songs found.")
                } else {
                    while(cursor.moveToNext()) { // loop thru rows in DB until found all songs in query - do one row at a time
                        // get the actual values stored in cols
                        val id = getLong(idColIdx)
                        val displayName = getString(displayNameColIdx)
                        val data = getString(dataColIdx)
                        val duration = getInt(durationColIdx)
                        val title = getString(titleColIdx)
                        val album = getString(albumColIdx)
                        val artist = getString(artistColIdx)
                        val genre = getString(genreColIdx)

                        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                        // Make a new Audio instance(ie: new song) & save into list to return
                        audioList += Audio(
                            uri, id, displayName, data, duration, title, album, artist, genre
                        )
                    }
                }
            }
        }

        return audioList
    }
}