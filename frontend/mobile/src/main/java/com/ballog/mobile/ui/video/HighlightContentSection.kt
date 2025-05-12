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
import com.ballog.mobile.ui.components.*
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
    onTogglePlayer: () -> Unit
) {
    Column {
        VideoPlaceholderBox(
            videoUri = videoUri,
            showPlayer = showPlayer,
            onTogglePlayer = onTogglePlayer
        )

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(20.dp))

            QuarterDropDown(
                selectedQuarter = selectedQuarter,
                expanded = expanded,
                onQuarterChange = onQuarterChange,
                onExpandedChange = onExpandedChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (videoUri == null) {
                BallogButton(
                    onClick = onUploadClick,
                    type = ButtonType.BOTH,
                    buttonColor = ButtonColor.GRAY,
                    icon = painterResource(id = R.drawable.ic_upload),
                    label = "영상 업로드",
                    modifier = Modifier.fillMaxWidth().height(48.dp)
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
    onTogglePlayer: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Gray.Gray300)
            .clickable(enabled = videoUri != null) { onTogglePlayer() }
    ) {
        if (videoUri != null) {
            if (showPlayer) {
                val exoPlayer = remember(videoUri) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(videoUri))
                        prepare()
                        playWhenReady = false
                    }
                }

                DisposableEffect(videoUri) {
                    onDispose {
                        exoPlayer.release()
                    }
                }

                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            player = exoPlayer
                            useController = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // 🔽 변경 포인트: key 사용해서 AsyncImage 강제 재렌더링
                key(videoUri) {
                    AsyncImage(
                        model = videoUri,
                        contentDescription = "Video Thumbnail",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}



@Composable
private fun QuarterDropDown(
    selectedQuarter: String,
    expanded: Boolean,
    onQuarterChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    DropDown(
        items = listOf("1 쿼터", "2 쿼터", "3 쿼터", "4 쿼터"),
        selectedItem = selectedQuarter,
        onItemSelected = onQuarterChange,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    )
}
