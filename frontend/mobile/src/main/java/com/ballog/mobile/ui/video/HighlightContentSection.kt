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
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ballog.mobile.R
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.DropDown
import com.ballog.mobile.ui.theme.Gray
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ballog.mobile.viewmodel.VideoViewModel
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            showPlayer = showPlayer,
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
    
    // 플레이어 상태 관리
    var playerReady by remember { mutableStateOf(false) }
    var videoLoading by remember { mutableStateOf(false) }
    var isVisibleState by remember { mutableStateOf(false) }
    
    // 디버깅 로그 - 컴포넌트 진입 시 상태 기록
    LaunchedEffect(Unit) {
        Log.d("VideoPlaceholderBox", "🔍 컴포넌트 진입 - URI: ${videoUri?.toString()?.take(20)}, showPlayer: $showPlayer")
    }
    
    // 쿼터 변경 감지 - 쿼터 변경 시 강제 로딩 상태로 설정
    var lastQuarter by remember { mutableStateOf("") }
    
    // 쿼터 변경 시 위치 초기화 필요 여부
    var shouldResetPosition by remember { mutableStateOf(false) }
    
    LaunchedEffect(selectedQuarter) {
        if (lastQuarter.isNotEmpty() && lastQuarter != selectedQuarter) {
            Log.d("VideoPlaceholderBox", "🔄 쿼터 변경 감지: $lastQuarter -> $selectedQuarter")
            videoLoading = true
            playerReady = false
            shouldResetPosition = true
        }
        lastQuarter = selectedQuarter
    }
    
    // 현재 처리 중인 URI 저장
    var currentVideoUri by remember { mutableStateOf<Uri?>(null) }
    
    // 쿼터 변경 감지용 키
    val videoKey = remember(videoUri, selectedQuarter) {
        "${selectedQuarter}_${videoUri?.toString() ?: "empty"}_${System.currentTimeMillis()}"
    }
    
    // 비디오 URI나 쿼터가 변경되면 isVisibleState 초기화
    LaunchedEffect(videoKey) {
        isVisibleState = false
        
        // 500ms 후에 강제로 isVisibleState 확인하고 여전히 false면 다시 로딩 트리거
        kotlinx.coroutines.delay(500)
        if (!isVisibleState && !videoLoading && videoUri != null) {
            Log.d("VideoPlaceholderBox", "⚠️ 썸네일 로드 지연 감지 - 강제 리로드 트리거")
            isVisibleState = false
        }
    }
    
    // showPlayer 상태가 false로 변경될 때(썸네일 모드로 전환될 때) isVisibleState 초기화
    LaunchedEffect(showPlayer) {
        if (!showPlayer) {
            // 0.1초 후 isVisibleState를 false로 설정하여 AsyncImage를 강제로 다시 로드
            kotlinx.coroutines.delay(100)
            isVisibleState = false
        }
    }
    
    // 플레이어 초기화 - 쿼터나 비디오 URI가 변경될 때마다 새로 생성
    val exoPlayer = remember(videoKey) {
        Log.d("VideoPlaceholderBox", "🔄 ExoPlayer 재생성: $selectedQuarter, URI: ${videoUri?.toString()?.take(20)}...")
        videoLoading = true
        playerReady = false
        shouldResetPosition = true
        
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
            // 새 ExoPlayer 생성 시 항상 위치를 0으로 설정
            seekTo(0)
        }
    }
    
    // ViewModel에 ExoPlayer 설정
    LaunchedEffect(exoPlayer) {
        viewModel.setCurrentExoPlayer(exoPlayer)
    }
    
    // ExoPlayer 해제 관리
    DisposableEffect(videoKey) {
        onDispose {
            Log.d("VideoPlaceholderBox", "🎵 ExoPlayer 해제: $selectedQuarter")
            exoPlayer.release()
        }
    }
    
    // 플레이어 해제 요청 처리
    val shouldReleasePlayer by viewModel.shouldReleasePlayer.collectAsState()
    LaunchedEffect(shouldReleasePlayer) {
        if (shouldReleasePlayer) {
            exoPlayer.apply {
                stop()
                clearMediaItems()
                release()
            }
            viewModel.resetPlayerRelease()
            Log.d("VideoPlaceholderBox", "🎵 해제 요청에 의한 ExoPlayer 해제 완료")
        }
    }
    
    // 비디오 URI 변경 감지 및 처리
    LaunchedEffect(videoKey) {
        if (videoUri == null) {
            return@LaunchedEffect
        }
        
        if (videoUri == currentVideoUri && playerReady && !shouldResetPosition) {
            Log.d("VideoPlaceholderBox", "⏭️ 같은 비디오 URI 감지, 로딩 스킵: $selectedQuarter")
            return@LaunchedEffect
        }
        
        currentVideoUri = videoUri
        videoLoading = true
        playerReady = false
        isVisibleState = false
        
        Log.d("VideoPlaceholderBox", "🎬 비디오 로드 시작: 쿼터=$selectedQuarter, URI=${videoUri.toString().take(20)}...")
        
        exoPlayer.apply {
            stop()
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(videoUri))
            // 미디어 아이템 설정 시 항상 위치를 0으로 초기화
            seekTo(0)
            prepare()
        }
    }
    
    // 플레이어 상태 리스너
    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_READY -> {
                        videoLoading = false
                        playerReady = true
                        isVisibleState = true
                        
                        // 재생 준비 완료 시 항상 시작 위치로 이동
                        if (shouldResetPosition) {
                            exoPlayer.seekTo(0)
                            shouldResetPosition = false
                            Log.d("VideoPlaceholderBox", "⏮️ 비디오 위치 초기화: $selectedQuarter")
                        }
                        
                        Log.d("VideoPlaceholderBox", "✅ 비디오 준비 완료: $selectedQuarter")
                    }
                    Player.STATE_BUFFERING -> {
                        videoLoading = true
                        Log.d("VideoPlaceholderBox", "⏳ 비디오 버퍼링 중: $selectedQuarter")
                    }
                    Player.STATE_ENDED -> {
                        Log.d("VideoPlaceholderBox", "🔚 비디오 재생 완료: $selectedQuarter")
                    }
                    Player.STATE_IDLE -> {
                        Log.d("VideoPlaceholderBox", "🔄 비디오 플레이어 초기화: $selectedQuarter")
                    }
                }
            }
        })
    }

    // 비디오 영역 UI
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(androidx.compose.ui.graphics.Color.Black)
            .clickable(
                enabled = videoUri != null && !videoLoading, // 로딩 중에는 클릭 비활성화
                onClick = onTogglePlayer
            )
    ) {
        if (videoUri == null) {
            // 비디오가 없는 경우
            return@Box
        }
        
        // 비디오 콘텐츠 (플레이어 또는 썸네일)
        if (showPlayer) {
            // 플레이어 모드
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        player = exoPlayer
                        useController = true
                        setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = {
                    it.player = exoPlayer
                }
            )
        } else {
            // 썸네일 모드
            VideoThumbnail(
                videoUri = videoUri,
                selectedQuarter = selectedQuarter,
                onThumbnailLoaded = { isVisibleState = true }
            )
        }
        
        // 로딩 인디케이터 - 비디오 로딩 중일 때 항상 표시 (플레이어 모드와 썸네일 모드 모두)
        if (videoLoading) {
            isVisibleState = false
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 1.0f)), // 완전 불투명 배경
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 3.dp
                )
            }
        } else if (!showPlayer && !isVisibleState) {
            // 로딩이 끝났지만 썸네일이 아직 로드되지 않은 경우 강제로 썸네일 로드 트리거
            VideoThumbnail(
                videoUri = videoUri,
                selectedQuarter = selectedQuarter,
                fallbackColor = androidx.compose.ui.graphics.Color.Black,
                onThumbnailLoaded = { isVisibleState = true },
                onThumbnailLoadFailed = { videoLoading = false }
            )
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

// 함수 끝에 유틸리티 확장 함수 추가
private fun ImageRequest.Builder.placeholderOf(videoUri: Uri?): ImageRequest.Builder {
    // 시스템 콘텐츠 프로바이더를 통한 비디오 URI인 경우에만 처리
    if (videoUri?.scheme == "content") {
        // 첫 번째 프레임을 미리 로드하는 설정 추가
        this.size(width = 800, height = 450) // 16:9 비율의 적당한 크기
    }
    return this
}

/**
 * 비디오 썸네일을 효율적으로 로드하는 컴포저블
 * MediaMetadataRetriever와 AsyncImage를 함께 사용하여 다양한 방식으로 썸네일 획득 시도
 */
@Composable
private fun VideoThumbnail(
    videoUri: Uri?,
    selectedQuarter: String,
    fallbackColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
    onThumbnailLoaded: () -> Unit = {},
    onThumbnailLoadFailed: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // 썸네일 상태
    var thumbnailBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val isFirstAttempt = remember { mutableStateOf(true) }
    
    // 컴포넌트 마운트 시 즉시 썸네일 생성 시도
    LaunchedEffect(Unit) {
        if (videoUri != null) {
            Log.d("VideoThumbnail", "👉 컴포넌트 마운트 시 즉시 썸네일 생성 시도: ${videoUri.toString().take(20)}")
        }
    }
    
    // 비디오 URI나 쿼터가 변경되면 썸네일 초기화
    LaunchedEffect(videoUri, selectedQuarter) {
        if (videoUri == null) return@LaunchedEffect
        
        try {
            isFirstAttempt.value = true
            thumbnailBitmap = null

            // 백그라운드 스레드에서 썸네일 추출
            val bitmap = withContext(Dispatchers.IO) {
                extractThumbnail(context, videoUri)
            }
            
            if (bitmap != null) {
                thumbnailBitmap = bitmap.asImageBitmap()
                Log.d("VideoThumbnail", "✅ 썸네일 비트맵 변환 완료")
                onThumbnailLoaded()
            } else {
                Log.d("VideoThumbnail", "⚠️ 직접 추출 실패, AsyncImage로 대체")
                isFirstAttempt.value = false
            }
        } catch (e: Exception) {
            Log.e("VideoThumbnail", "❌ 썸네일 처리 과정 중 오류", e)
            isFirstAttempt.value = false
            onThumbnailLoadFailed()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 직접 추출한 비트맵이 있으면 표시
        if (thumbnailBitmap != null) {
            Image(
                bitmap = thumbnailBitmap!!,
                contentDescription = "비디오 썸네일",
                modifier = Modifier.fillMaxSize()
            )
        } 
        // 직접 추출 실패한 경우 AsyncImage로 대체
        else if (!isFirstAttempt.value) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(videoUri)
                    .crossfade(true)
                    .placeholderOf(videoUri)
                    .build(),
                contentDescription = "비디오 썸네일 대체",
                modifier = Modifier
                    .fillMaxSize()
                    .background(fallbackColor),
                onSuccess = {
                    Log.d("VideoThumbnail", "✅ AsyncImage 썸네일 로드 성공: $selectedQuarter")
                    onThumbnailLoaded()
                },
                onError = {
                    Log.e("VideoThumbnail", "❌ AsyncImage 썸네일 로드 실패: $selectedQuarter")
                    onThumbnailLoadFailed()
                }
            )
        } else {
            // 로딩 중 상태 - 아무것도 표시하지 않음
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(fallbackColor)
            )
        }
    }
}

/**
 * 비디오에서 썸네일을 추출하는 헬퍼 함수
 */
private fun extractThumbnail(context: android.content.Context, videoUri: Uri): Bitmap? {
    try {
        Log.d("VideoThumbnail", "🖼️ 직접 추출 시도: ${videoUri.toString().take(20)}...")
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        
        // 여러 시간대에서 썸네일 획득 시도
        val frames = listOf(
            0L, 
            1000000L, // 1초
            3000000L  // 3초
        )
        
        var resultBitmap: Bitmap? = null
        
        for (timeUs in frames) {
            try {
                // API 레벨 따라 다른 메서드 사용
                resultBitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
                } else {
                    retriever.getFrameAtTime(timeUs)
                }
                
                if (resultBitmap != null) {
                    Log.d("VideoThumbnail", "✅ ${timeUs/1000000}초 지점에서 프레임 획득 성공")
                    break
                }
            } catch (e: Exception) {
                Log.e("VideoThumbnail", "❌ ${timeUs/1000000}초 지점 프레임 획득 실패", e)
            }
        }
        
        // 마지막 시도: 미디어 메타데이터에서 썸네일 사용하기
        if (resultBitmap == null) {
            Log.d("VideoThumbnail", "🔍 임베디드 썸네일 시도")
            resultBitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                retriever.getFrameAtIndex(0)
            } else {
                retriever.frameAtTime
            }
        }
        
        // 정리
        retriever.release()
        return resultBitmap
    } catch (e: Exception) {
        Log.e("VideoThumbnail", "❌ 썸네일 추출 실패: ${e.message}")
        return null
    }
}
