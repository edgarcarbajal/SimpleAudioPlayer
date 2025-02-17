package com.example.simpleaudioplayer.views

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.simpleaudioplayer.R
import com.example.simpleaudioplayer.models.Audio
import kotlin.math.floor

private fun currDurationToTimestamp(duration: Long): String {
    val totalTruncatedSec = floor(duration / 1E3).toInt()
    val totalTruncatedMin = totalTruncatedSec / 60
    val remainingSec = totalTruncatedSec - (totalTruncatedMin * 60)

    return if(duration < 0) "--:--"
    else String.format("%d:%02d", totalTruncatedMin, remainingSec)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicAudioHome(
    progressString: String,
    progress: Float,
    onProgress: (Float) -> Unit, // function param that takes a float, and returns a Unit
    isAudioPlaying: Boolean,
    currentAudio: Audio,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onItemClick: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    artwork: Bitmap?
) {
    // Logic/State vars for Sheet - Should move later on to a view model to decouple logic??? Not sure
    var showMusicPlayerSheet by remember {
        mutableStateOf(false)
    }
    val musicSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var searchText by remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current // Get focus to remove focus from searchbar when clicking outside of it
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold (
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection) // Needed In order for TopAppBar to collapse when scrolling
            .clickable {
                focusManager.clearFocus()
            },
        topBar = {
            CenterAlignedTopAppBar(
                expandedHeight = TopAppBarDefaults.MediumAppBarExpandedHeight,
                title = {
                    TextField(
                        searchText,
                        onValueChange = {searchText = it},
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            focusManager.clearFocus()
                        }),
                        singleLine = true, // TODO: Cursor does not keep scrolling in either direction if there is still text to see in the textfield but cutoff by the view constraints
                                        // After further testing, You can scroll the text left or right. Just not with the text cursor. pretend it has an invisible horizontal scroll bar and drag it left or right. Not sure still how to make the text cursor to scroll the field...
                        label = { Text("Search") },
                        trailingIcon = {
                            IconButton(onClick = {
                                searchText = ""
                            }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear Search-Text Button",
                                )
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                    )
                },
                actions = {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "UI Search Button"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            BottomBarPlayer( // Minimized Audio Player - (might Modify this into a Sheet/card (not sure if Modal or not))
                modifier = Modifier.clickable { showMusicPlayerSheet = true },
                audio = currentAudio,
                progressString = progressString,
                progress = progress,
                onProgress = onProgress,
                isAudioPlaying = isAudioPlaying,
                onStart = onStart,
                onNext = onNext,
                onPrevious = onPrevious,
            )
            if(showMusicPlayerSheet) {
                ExpandedMusicBarSheet(
                    musicSheetState,
                    toggleSheetExpansion = { showMusicPlayerSheet = !showMusicPlayerSheet} // Toggle Showing the Expanded Music Player Sheet when dismissing the sheet
                ) {
                    ExpandedMusicBarInfo(
                        audio = currentAudio,
                        artwork = artwork,
                        progressString = progressString,
                        progress = progress,
                        onProgress = onProgress,
                        isAudioPlaying = isAudioPlaying,
                        onStart = onStart,
                        onNext = onNext,
                        onPrevious = onPrevious,
                    )
                }
            }
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ExpandedMusicBarInfo(
    audio: Audio,
    artwork: Bitmap?,
    progressString: String,
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .clip(MaterialTheme.shapes.large),
                contentAlignment = Alignment.Center
            ) {
                GlideImage(
                    model = artwork,
                    contentDescription = "Current Audio Album Art",
                    loading = placeholder(R.drawable.noart),
                    failure = placeholder(R.drawable.ic_launcher_foreground),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = audio.title,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = audio.album,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = audio.artist,
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.size(8.dp))

            ExpandedMediaPlayerController(
                progressString = progressString,
                durationString = currDurationToTimestamp(audio.duration.toLong()),
                progress = progress,
                onProgress = onProgress,
                isAudioPlaying = isAudioPlaying,
                onStart = onStart,
                onNext = onNext,
                onPrevious = onPrevious,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedMusicBarSheet(
    musicSheetState: SheetState,
    toggleSheetExpansion: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit) // Used for when we want to place another View/Composable inside the Sheet
) {
    ModalBottomSheet(
        onDismissRequest = {
            //showMusicPlayerSheet = false
            toggleSheetExpansion()
        },
        sheetState = musicSheetState,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 16.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .width(50.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    ) {
       content()
    }
}

@Composable
fun BottomBarPlayer(
    progressString: String,
    modifier: Modifier,
    audio: Audio,
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    BottomAppBar (
        modifier = modifier,
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

                    Text(
                        text = String.format("%s/%s", progressString, currDurationToTimestamp(audio.duration.toLong()))
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
fun ExpandedMediaPlayerController(
    progressString: String,
    durationString: String,
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Card(
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    modifier = Modifier
                        .clickable { onPrevious() }
                        .size(50.dp),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.padding(16.dp, 0.dp))

                PlayerIconItem(
                    modifier = Modifier.size(50.dp),
                    icon = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
                ) {
                    onStart() // toggle start/stop of Audio
                }

                Spacer(modifier = Modifier.padding(16.dp, 0.dp))

                Icon(
                    imageVector = Icons.Default.SkipNext,
                    modifier = Modifier
                        .clickable { onNext() }
                        .size(50.dp),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = String.format("%s/%s", progressString, durationString)
            )
            // UI for seeking to given pos in audio timeline
            Slider(
                modifier = Modifier
                    .padding(2.dp),
                //.weight(1f),
                value = progress,
                onValueChange = {onProgress(it)},
                valueRange = 0f..1f,
            )
        }
    }
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
                if (isCurrentAudio)
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
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
               text = currDurationToTimestamp(audio.duration.toLong()),
               style = MaterialTheme.typography.bodySmall,
               overflow = TextOverflow.Ellipsis,
               maxLines = 1,
           )
           Spacer(modifier = Modifier.size(8.dp))
       }
    }
}

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
        Spacer(modifier = Modifier.size(4.dp))

        val focusRequester = remember{ FocusRequester() }
        Column(
            modifier = Modifier
                .basicMarquee( // these 4 lines should add a focus animation where overflow text scrolls when focused on
                    animationMode = MarqueeAnimationMode.WhileFocused,
                    repeatDelayMillis = 1000
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
            .then(modifier)
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
                modifier = modifier,
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
fun ExpandedMusicBarInfo_Preview() {
    ExpandedMusicBarInfo(
        audio = dummyAudio2,
        artwork = null,
        progressString = "2:20",
        progress = 0.4f,
        onProgress = { /*TODO*/ },
        isAudioPlaying = true,
        onStart = { /*TODO*/ },
        onNext = { /*TODO*/ },
        onPrevious = { /*TODO*/ },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ExpandedMusicBarSheet_Preview() {
    // Need to run Interactive Mode inorder to see preview!
    ExpandedMusicBarSheet(
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        toggleSheetExpansion = {}
    ){}
}


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
        modifier = Modifier,
        audio = dummyAudio,
        progressString = "0:00",
        progress = 0.5f,
        onProgress = { /*TODO*/ },
        isAudioPlaying = false,
        onStart = { /*TODO*/ },
        onNext = { /*TODO*/ },
        onPrevious = { /*TODO*/ },
    )
}

@Preview(showSystemUi = true)
@Composable
fun BasicAudioHome_Preview() {
    BasicAudioHome(
        progressString = "0:00",
        progress = 0.5f,
        onProgress = { /*TODO*/ },
        isAudioPlaying = true,
        currentAudio = dummyAudio,
        audioList = listOf(dummyAudio, dummyAudio2),
        onStart = { /*TODO*/ },
        onItemClick = { /*TODO*/ },
        onNext = { /*TODO*/ },
        onPrevious = { /*TODO*/ },
        artwork = null,
    )
}