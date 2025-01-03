# SimpleAudioPlayer - Android App

This repo is the code for a simple audio player in android using the `androidx.media3` library to do so.
This is a simple project mostly to learn how to code android apps using Kotlin, and Jetpack Compose.
I have learned how to make basic iOS apps using Swift, so I will be trying to translate what I learned there to here.

Since this is a learning project, the project is mostly the same as the video [here](https://www.youtube.com/watch?v=XrcmjIW45u8) from HoodLab, with some modifications/ 
additions to my part.

Also used the documentation for Media3 from Google, sparingly however, since the documentation is more geared towards veteran Android developers, so I found it somewhat 
vague on how to setup Media3 components to play Audio files, and connect these components to Jetpack Compose views.

A future app where I build a video player by myself, using this project (when completed) as a guide, might be in the works later!

## Running the code
For best experience, please use Android Studio to run the code.


## Current Medium-Major Issues/ TODO List (not sure if all fixable):
- Issue with Parcel library that causes app to crash at times: 
```
java.lang.illegalargumentexception: Parcel: unknown type for value Audio(...)
```
>The location where this line shows up does not say but I assume is around the `AudioContentRetriever` or `AudioViewModel`. It also happens often when large amount of audio files(just due to the fact of more opportunity to fail due to creating more Audio objects)

- Background play of audio is not implemented, or is missing? (Need to check this one for sure)

- App does not handle well with a large list of audio files (Not sure the cutoff point - Tested 1 device with around 600+ files)

- It takes a while to load the audio list when large (5-15 sec) after accepting permissions (Might even require a few app reboots)

- Some songs show playback when they are not playing back audio (only in large audio lists) Happens frequently when skipping/changing to other songs 

