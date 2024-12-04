package com.example.simpleaudioplayer.services

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import javax.inject.Inject

@UnstableApi
class AudioNotificationAdapter @Inject constructor(
    private val context: Context,
    private val pendingIntent: PendingIntent?,
):PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence = player.mediaMetadata.albumTitle ?: "Unknown Audio Album Title"

    override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent

    override fun getCurrentContentText(player: Player): CharSequence? = player.mediaMetadata.displayTitle ?: "Unknown Audio Title"

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        Glide.with(context)
            .asBitmap()
            .load(player.mediaMetadata.artworkUri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap>(){ //overload class to fit with this impl
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback.onBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit // ie: return void

            })
        return null // returns null (only if return void does not happen?)
    }

}