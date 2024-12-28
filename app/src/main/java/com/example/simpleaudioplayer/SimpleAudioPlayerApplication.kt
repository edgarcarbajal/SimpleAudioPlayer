package com.example.simpleaudioplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // Runs/builds application with Hilt(installs needed dependencies) - Need to specify this class name inside of AndroidManifest application tag
class SimpleAudioPlayerApplication: Application() {
}