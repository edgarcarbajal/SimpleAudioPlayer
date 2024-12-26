package com.example.simpleaudioplayer.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.simpleaudioplayer.models.Audio
import com.example.simpleaudioplayer.services.AudioState
import kotlin.math.floor

private fun currPosToTimestamp(position: Long): String {
    val totalTruncatedSec = floor(position / 1E3).toInt()
    val totalTruncatedMin = totalTruncatedSec / 60
    val remainingSec = totalTruncatedSec - (totalTruncatedMin * 60)

    return if(position < 0) "--:--"
    else String.format("%d:%02d", totalTruncatedMin, remainingSec)
}

@Composable
fun BasicAudioHome(
    progress: Float,
    onProgress: (Float) -> Unit, // function param that takes a float, and returns a Unit
    isAudioPlaying: Boolean,
    currentAudio: Audio,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onItemClick: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    Scaffold (
        bottomBar = {
            BottomBarPlayer( // Minimized Audio Player - (might Modify this into a Sheet/card (not sure if Modal or not))
                audio = currentAudio,
                progress = progress,
                onProgress = onProgress,
                isAudioPlaying = isAudioPlaying,
                onStart = onStart,
                onNext = onNext,
                onPrevious = onPrevious,
            )
        },
        ){
        LazyColumn(contentPadding = it) {
            itemsIndexed(audioList){index, audioItem ->
                AudioItemCard(
                    isCurrentAudio = currentAudio == audioItem,
                    audio = audioItem,
                    onItemClick = {
                        onItemClick(index)
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBarPlayer(
    audio: Audio,
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    BottomAppBar (
        content = {
            Column(
                modifier = Modifier.padding(8.dp)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    ArtistInfo(
                        modifier = Modifier.weight(1f),
                        audio = audio,
                    )
                    MediaPlayerController( // UI for PlayPauseSkip
                        isAudioPlaying = isAudioPlaying,
                        onStart = onStart,
                        onNext = onNext,
                        onPrevious = onPrevious
                    )

                    // UI for seeking to given pos in audio timeline
                    Slider(
                        modifier = Modifier.weight(1f),
                        value = progress,
                        onValueChange = {onProgress(it)},
                        valueRange = 0f..1f,
                    )
                }
            }
        }
    )
}

@Composable
fun MediaPlayerController(
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .padding(4.dp),
    ){
        PlayerIconItem(
            icon = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
        ) {
            onStart() // toggle start/stop of Audio
        }

        Spacer(modifier = Modifier.size(8.dp))

        Icon(
            imageVector = Icons.Default.SkipPrevious,
            modifier = Modifier.clickable { onPrevious() },
            contentDescription = null
        )

        Spacer(modifier = Modifier.padding(4.dp, 0.dp))

        Icon(
            imageVector = Icons.Default.SkipNext,
            modifier = Modifier.clickable { onNext() },
            contentDescription = null
        )
    }
}

@Composable
fun AudioItemCard(
    isCurrentAudio: Boolean,
    audio: Audio,
    onItemClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .then(
                if(isCurrentAudio)
                    Modifier.border(width = 2.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp))
                else
                    Modifier
            )
            .clickable {
                onItemClick()
            }
    ) {
       Row(
           verticalAlignment = Alignment.CenterVertically
       ) {
           Column(
               modifier = Modifier
                   .weight(1f)
                   .padding(8.dp),
               verticalArrangement = Arrangement.Center
           ) {
               Spacer(modifier = Modifier.size(4.dp))
               Text(
                   text = audio.title,
                   style = MaterialTheme.typography.titleLarge,
                   overflow = TextOverflow.Ellipsis,
                   maxLines = 1,
               )

               Spacer(modifier = Modifier.size(4.dp))
               Text(
                   text = audio.displayName,
                   style = MaterialTheme.typography.labelSmall,
                   overflow = TextOverflow.Ellipsis,
                   maxLines = 1,
               )

               Spacer(modifier = Modifier.size(4.dp))
               Text(
                   text = audio.artist,
                   style = MaterialTheme.typography.bodySmall,
                   overflow = TextOverflow.Ellipsis,
                   maxLines = 1,
               )
           }
           Text(
               text = currPosToTimestamp(audio.duration.toLong()),
               style = MaterialTheme.typography.bodySmall,
               overflow = TextOverflow.Ellipsis,
               maxLines = 1,
           )
           Spacer(modifier = Modifier.size(8.dp))
       }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistInfo(
    modifier: Modifier = Modifier,
    audio: Audio,
) {
    Row(
        modifier = modifier.then(
            Modifier.padding(4.dp)
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerIconItem(
            icon = Icons.Default.MusicNote,
            borderStroke = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        ){}
        
        Spacer(modifier = Modifier.size(4.dp))

        val focusRequester = remember{ FocusRequester() }
        Column(
            modifier = Modifier
                .basicMarquee( // these 4 lines should add a focus animation where overflow text scrolls when focused on
                    animationMode = MarqueeAnimationMode.WhileFocused,
                    delayMillis = 1000
                )
                .focusRequester(focusRequester)
                .focusable()
                .clickable { focusRequester.requestFocus() },
        ) {
            Text(
                text = audio.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Ellipsis,

                modifier = Modifier
                    .weight(1f),// needed to give extra space to the larger font??
                maxLines = 1,
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = audio.artist,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun PlayerIconItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    borderStroke: BorderStroke? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    foregroundColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
    ) {
    Surface(
        shape = CircleShape,
        border = borderStroke,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() },
        contentColor = foregroundColor,
        color = backgroundColor,
    ) {
        Box(
            modifier = Modifier.padding(4.dp),
            contentAlignment = Alignment.Center
            ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }
    }
}


// Previews:
val dummyAudio = Audio(
    "".toUri(),
    0L,
    "dummy_audio.flac",
    "",
    0,
    "Merry Christmas",
    "Jolly Holidays Vol. 1",
    "Random artist",
    "Holiday",
)
val dummyAudio2 = Audio(
    "".toUri(),
    1L,
    "dummy_audio2.mp4",
    "",
    350000,
    "Please Christmas Don't Be Late",
    "Jolly Holidays Vol. 1",
    "Random artist",
    "Holiday",
)

@Preview(showBackground = true)
@Composable
fun AudioItemCard_Preview() {
    AudioItemCard(isCurrentAudio = true, audio = dummyAudio) {}
}

@Preview(showBackground = true)
@Composable
fun ArtistInfo_Preview() {
    ArtistInfo(audio = dummyAudio)
}

@Preview(showBackground = true)
@Composable
fun BottomBarPlayer_Preview() {
    BottomBarPlayer(
        audio = dummyAudio,
        progress = 0.5f,
        onProgress = { /*TODO*/ },
        isAudioPlaying = false,
        onStart = { /*TODO*/ },
        onNext = { /*TODO*/ },
    ) {}
}

@Preview(showSystemUi = true)
@Composable
fun BasicAudioHome_Preview() {
    BasicAudioHome(
        progress = 0.5f,
        onProgress = { /*TODO*/ },
        isAudioPlaying = true,
        currentAudio = dummyAudio,
        audioList = listOf(dummyAudio, dummyAudio2),
        onStart = { /*TODO*/ },
        onItemClick = { /*TODO*/ },
        onNext = { /*TODO*/ },
        onPrevious = { /*TODO*/ },
    )
}