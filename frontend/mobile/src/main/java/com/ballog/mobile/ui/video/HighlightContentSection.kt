package com.ballog.mobile.ui.video

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.ballog.mobile.R
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.DropDown
import com.ballog.mobile.ui.theme.Gray

@Composable
fun HighlightContentSection(
    videoUri: Uri?,
    highlights: List<HighlightUiState>,
    showPlayer: Boolean,
    selectedQuarter: String,
    expanded: Boolean,
    onQuarterChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (HighlightUiState) -> Unit,
    onDeleteVideo: () -> Unit,
    onUploadClick: () -> Unit,
    onTogglePlayer: () -> Unit,
    quarterOptions: List<String>
) {
    Column {
        VideoPlaceholderBox(
            videoUri = videoUri,
            showPlayer = showPlayer,
            onTogglePlayer = onTogglePlayer,
            selectedQuarter = selectedQuarter
        )

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(20.dp))

            QuarterDropDown(
                selectedQuarter = selectedQuarter,
                expanded = expanded,
                onQuarterChange = onQuarterChange,
                onExpandedChange = onExpandedChange,
                quarterOptions = quarterOptions
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (videoUri == null) {
                BallogButton(
                    onClick = onUploadClick,
                    type = ButtonType.BOTH,
                    buttonColor = ButtonColor.GRAY,
                    icon = painterResource(id = R.drawable.ic_upload),
                    label = "영상 업로드",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            } else {
                highlights.forEach { highlight ->
                    HighlightCard(
                        title = highlight.title,
                        startTime = "${highlight.startHour}:${highlight.startMin}",
                        endTime = "${highlight.endHour}:${highlight.endMin}",
                        onEdit = { onEditClick(highlight) },
                        onLike = { /* TODO */ }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                BallogButton(
                    onClick = onAddClick,
                    type = ButtonType.BOTH,
                    buttonColor = ButtonColor.GRAY,
                    icon = painterResource(id = R.drawable.ic_add),
                    label = "구간 추가",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    color = Gray.Gray300,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                BallogButton(
                    onClick = onDeleteVideo,
                    type = ButtonType.BOTH,
                    buttonColor = ButtonColor.ALERT,
                    icon = painterResource(id = R.drawable.ic_trash),
                    label = "영상 삭제",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun VideoPlaceholderBox(
    videoUri: Uri?,
    showPlayer: Boolean,
    onTogglePlayer: () -> Unit,
    selectedQuarter: String
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var thumbnailUri by remember(videoUri, selectedQuarter) { mutableStateOf(videoUri) }

    val exoPlayer = remember(selectedQuarter) {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
        }
    }

    DisposableEffect(selectedQuarter) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(videoUri, selectedQuarter) {
        isLoading = true
        thumbnailUri = videoUri

        videoUri?.let {
            exoPlayer.apply {
                stop()
                clearMediaItems()
                setMediaItem(MediaItem.fromUri(it))
                prepare()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Gray.Gray300)
            .clickable(enabled = videoUri != null) { onTogglePlayer() }
    ) {
        if (videoUri != null) {
            if (showPlayer) {
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            player = exoPlayer
                            useController = true
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { it.player = exoPlayer }
                )
            } else {
                AsyncImage(
                    model = thumbnailUri,
                    contentDescription = "Video Thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    onLoading = { isLoading = true },
                    onSuccess = { isLoading = false },
                    onError = { isLoading = false }
                )
            }
        }
    }
}

@Composable
private fun QuarterDropDown(
    selectedQuarter: String,
    expanded: Boolean,
    onQuarterChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    quarterOptions: List<String>
) {
    DropDown(
        items = quarterOptions,
        selectedItem = selectedQuarter,
        onItemSelected = onQuarterChange,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    )
}
