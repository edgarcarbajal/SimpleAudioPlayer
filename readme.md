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

---

## Current Medium-Major Issues/ TODO List (not sure if all fixable):
- **(DONE)** Issue with Parcel library that causes app to crash at times: 
```
java.lang.illegalargumentexception: Parcel: unknown type for value Audio(...)
```
>After a bit of on-device manual testing, it seems like this error only appears when trying to exit the app (using home button/home gesture).   
>Must be something wrong setup with the AudioService that does not allow it to pass Audio items thru Parcel in the background?? Fix this, and background music playback should also be fixed.

> Update(1/13/2025) - Fixed: App was sending the Audio media items thru IPC in android system (using the `Intent` class/object. All objects need sent thru `Intent` need to be `Parcelable`. A little Similar to what iOS does when it needs to decode data from JSON or plist files - data class needs to implement `Decodable` in order to work.
> Thanks to [this blog post](https://prasanta-paul.blogspot.com/2010/06/android-parcelable-example.html) which helped me understand what I might needed to do.

- **(DONE FOR NOW)** App does not handle well with a large list of audio files (Not sure the cutoff point - Tested 1 device with around 600+ files)

- **(DONE FOR NOW)** It takes a while to load the audio list when large (5-15 sec) after accepting permissions (Might even require a few app reboots)

>Both items above (relating with speed of loading app) was found to be caused by retrieving album art
>when getting the list of audio files from device DB. To fix, going to implement a way so that we only get the album art of the current song (since that is the only one that can be seen in the UI anyway) using URI somehow?

> Update (1/13/2025): Fixed the 2 issues above by removing the artwork retrieval, tried making a new artwork retrieval, but still not working correctly
> Update (1/19/2025): In regards to the slight lag when scrolling, even with images now removed, this seems to be an issue with the implementation of `LazyColumn` in Jetpack Compose.
> There seems to be some ways to mitigate this lag, so I will be looking into ways I can maybe get rid of some of it at a later date.



- **(DONE!) Some songs show playback when they are not playing back audio (only in large audio lists) Happens frequently when skipping/changing to other songs 
> Something to note from the on-device manual testing, it seems like sometimes the same songs are the ones that do not load up? - maybe its a loading issue? (like when the songs were added in list?)
> 
> Update (1/19/2025): Figured out a few days back that Android (and by extension Media3 library) did not support ALAC encoded audio files by default
> which my test device had some, thus failing to play any audio while showing progress
> The way to resolve it was to use an extension encoder in Media3 to cover the encodings the default encoder lacked.
> 
> Used the library FFMPEG as an extension to do this, however, Google did not provide a Maven dependency repo (ie: an import) to the library; they require for the user to build Media3 with FFMPEG by source.
> I ran into the issue however, that I was not able to build FFMPEG with my laptop running MacOS due to an issue with missing `libiconv` symbols. [See here for more details.](https://stackoverflow.com/questions/57734434/libiconv-or-iconv-undefined-symbol-on-mac-osx)
> I could not seem to resolve it on my laptop, so I was about to build it on another computer running Linux instead when I found that a prebuilt library is maintained, and placed in a Maven repo by the
> FOSS Project **[Jellyfin](https://www.jellyfin.org/)**, so I decided to use their [Maven repo import](https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://central.sonatype.com/artifact/org.jellyfin.media3/media3-ffmpeg-decoder&ved=2ahUKEwilx8fPioOLAxUb5skDHRbXO2EQFnoECBIQAQ&usg=AOvVaw2tke5tib2L6tCSwX31sozI) instead for the simplicity!
> [The Github repo is here too.](https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://central.sonatype.com/artifact/org.jellyfin.media3/media3-ffmpeg-decoder&ved=2ahUKEwilx8fPioOLAxUb5skDHRbXO2EQFnoECBIQAQ&usg=AOvVaw2tke5tib2L6tCSwX31sozI)
> 
> Thank you to the maintainers at Jellyfin for allowing access to this library.

