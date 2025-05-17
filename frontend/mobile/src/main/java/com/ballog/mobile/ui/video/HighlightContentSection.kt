package com.ballog.mobile.ui.video

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ballog.mobile.viewmodel.VideoViewModel

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
    quarterOptions: List<String>,
    onHighlightClick: (String) -> Unit = {},
    viewModel: VideoViewModel = viewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        VideoPlaceholderBox(
            videoUri = videoUri,
            showPlayer = true,
            onTogglePlayer = onTogglePlayer,
            selectedQuarter = selectedQuarter,
            viewModel = viewModel
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
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
                        startTime = "${highlight.startMin}:${highlight.startSec}",
                        endTime = "${highlight.endMin}:${highlight.endSec}",
                        onEdit = { onEditClick(highlight) },
                        onLike = { /* TODO */ },
                        onClick = { onHighlightClick("${highlight.startMin}:${highlight.startSec}") }
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
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun VideoPlaceholderBox(
    videoUri: Uri?,
    showPlayer: Boolean,
    onTogglePlayer: () -> Unit,
    selectedQuarter: String,
    viewModel: VideoViewModel = viewModel()
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var isThumbnailLoading by remember { mutableStateOf(true) }
    var thumbnailUri by remember(videoUri, selectedQuarter) { mutableStateOf(videoUri) }
    
    val exoPlayer = remember(selectedQuarter) {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
        }
    }
    
    // ExoPlayer 인스턴스를 공유 상태에 저장
    viewModel.setCurrentExoPlayer(exoPlayer)

    val shouldReleasePlayer by viewModel.shouldReleasePlayer.collectAsState()

    DisposableEffect(selectedQuarter) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(shouldReleasePlayer) {
        if (shouldReleasePlayer) {
            exoPlayer.apply {
                stop()
                clearMediaItems()
                release()
            }
            viewModel.resetPlayerRelease()
            Log.d("VideoPlaceholderBox", "🎵 ExoPlayer 해제 완료")
        }
    }

    LaunchedEffect(videoUri, selectedQuarter) {
        isLoading = true
        isThumbnailLoading = true
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

    // 플레이어의 준비 상태를 추적
    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == androidx.media3.common.Player.STATE_READY) {
                    isLoading = false
                }
            }
        })
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
                // 로딩 중일 때는 검은색 배경만 표시
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(androidx.compose.ui.graphics.Color.Black),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 4.dp
                        )
                    }
                } else {
                    // 로딩이 완료되면 ExoPlayer 표시
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
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 썸네일 로딩 중일 때는 회색 배경만 표시
                    if (isThumbnailLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Gray.Gray300),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        // 썸네일 로딩이 완료되면 썸네일 표시
                        AsyncImage(
                            model = thumbnailUri,
                            contentDescription = "비디오 썸네일",
                            modifier = Modifier.fillMaxSize(),
                            onLoading = { isThumbnailLoading = true },
                            onSuccess = { isThumbnailLoading = false },
                            onError = { isThumbnailLoading = false }
                        )
                    }
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
