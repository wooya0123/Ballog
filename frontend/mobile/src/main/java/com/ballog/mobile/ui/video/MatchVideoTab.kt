package com.ballog.mobile.ui.video

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ballog.mobile.data.dto.HighlightAddRequest
import com.ballog.mobile.data.dto.HighlightUpdateRequest
import com.ballog.mobile.ui.components.LoadingDialog
import com.ballog.mobile.ui.theme.BallogTheme
import com.ballog.mobile.util.FileUtils
import com.ballog.mobile.util.VideoUtils
import com.ballog.mobile.viewmodel.VideoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchVideoTab(matchId: Int) {
    Log.d("MatchVideoTab", "🟦 $matchId 번 매치의 영상 탭 접속")
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // ViewModel과 상태 초기화
    val videoViewModel: VideoViewModel = viewModel()
    val videoUiState by videoViewModel.videoUiState.collectAsState()
    val isUploading by videoViewModel.isUploading.collectAsState()
    val isExtractingHighlights by videoViewModel.isExtractingHighlights.collectAsState()
    val currentVideoFile by videoViewModel.currentVideoFile.collectAsState()
    val error by videoViewModel.error.collectAsState()

    var selectedQuarter by remember { mutableStateOf("1 쿼터") }
    var expanded by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteVideoDialog by remember { mutableStateOf(false) }
    var editingHighlight by remember { mutableStateOf(HighlightUiState()) }
    var deleteVideoId by remember { mutableStateOf(-1) }

    // local state로 현재 선택된 쿼터의 정보를 관리 (showPlayer, videoUrl, highlights 등)
    var currentQuarterState by remember {
        mutableStateOf(
            QuarterVideoData(
                quarterNumber = selectedQuarter.filter { it.isDigit() }
                    .toIntOrNull() ?: 1
            )
        )
    }

    // BottomSheet 상태 (addSheetState와 editSheetState 사용)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 에러 다이얼로그 표시
    error?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { videoViewModel.setError(null) },
            title = { Text("오류") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { videoViewModel.setError(null) }) {
                    Text("확인")
                }
            }
        )
    }

    // 로딩 다이얼로그 표시
    if (isExtractingHighlights) {
        LoadingDialog(message = "영상 업로드 중...")
    }

    // 쿼터 옵션 계산
    val quarterOptions = remember(videoUiState.totalQuarters) {
        (1..videoUiState.totalQuarters).map { "$it 쿼터" }
    }

    // 매번 selectedQuarter에 따른 현재 쿼터 번호
    val selectedQuarterNumber = selectedQuarter.filter { it.isDigit() }.toIntOrNull() ?: 1

    // videoUiState 업데이트에 따라 현재 쿼터의 서버 데이터를 반영
    LaunchedEffect(videoUiState.quarterList, selectedQuarterNumber) {
        val serverQuarter = videoUiState.quarterList.find { it.quarterNumber == selectedQuarterNumber }
        // currentVideoFile을 local 변수에 할당해서 스마트 캐스트 사용
        val localFile = currentVideoFile
        currentQuarterState = when {
            isUploading && localFile != null ->
                QuarterVideoData(
                    quarterNumber = selectedQuarterNumber,
                    videoUrl = localFile.toURI().toString(),
                    showPlayer = true,
                    highlights = serverQuarter?.highlights ?: emptyList()
                )
            serverQuarter != null -> serverQuarter
            else -> QuarterVideoData(quarterNumber = selectedQuarterNumber)
        }
    }

    // 비디오 업로드 런처
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            Log.d("MatchVideoTab", "📁 영상 URI 선택됨: $uri")
            val file = FileUtils.uriToFile(context, it)
            val duration = VideoUtils.getVideoDurationString(context, it)
            val quarterNumber = selectedQuarter.filter { it.isDigit() }
                .toIntOrNull() ?: 1

            // local 상태 업데이트 (영상 업로드 관련 UI 반영)
            currentQuarterState = currentQuarterState.copy(
                videoUrl = it.toString(),
                showPlayer = true
            )

            Log.d("MatchVideoTab", "🚀 영상 업로드 시작 → matchId=$matchId, quarter=$quarterNumber")
            videoViewModel.uploadQuarterVideo(
                context = context,
                file = file,
                matchId = matchId,
                quarterNumber = quarterNumber,
                duration = duration
            )
        } ?: Log.w("MatchVideoTab", "⛔ 영상 URI가 null입니다.")
    }

    // 초기에 매치 영상 데이터 로드
    LaunchedEffect(Unit) {
        Log.d("MatchVideoTab", "🔄 초기 데이터 로드 시작")
        videoViewModel.getMatchVideos(matchId)
    }

    // 첫 진입 시 showPlayer가 false이면서 videoUrl이 있는 경우 -> 썸네일 표시 트리거
    LaunchedEffect(currentQuarterState) {
        if (currentQuarterState.videoUrl.isNotBlank() && !currentQuarterState.showPlayer) {
            Log.d("MatchVideoTab", "🔄 첫 렌더링 시 비디오 감지 - 썸네일 표시 준비")
            delay(200)
            currentQuarterState = currentQuarterState.copy(showPlayer = true)
            delay(500)
            currentQuarterState = currentQuarterState.copy(showPlayer = false)
        }
    }

    // videoUiState의 하이라이트 반영
    LaunchedEffect(videoUiState) {
        val updatedHighlights = videoUiState.quarterList.find { it.videoId == currentQuarterState.videoId }
            ?.highlights ?: return@LaunchedEffect
        currentQuarterState = currentQuarterState.copy(highlights = updatedHighlights)
        Log.d("MatchVideoTab", "🟢 하이라이트 반영 완료")
    }

    // UI 렌더링
    Column(modifier = Modifier.fillMaxSize()) {
        HighlightContentSection(
            videoUri = currentQuarterState.videoUrl.takeIf { it.isNotBlank() }?.let { Uri.parse(it) },
            highlights = currentQuarterState.highlights,
            showPlayer = currentQuarterState.showPlayer,
            selectedQuarter = selectedQuarter,
            quarterOptions = quarterOptions,
            expanded = expanded,
            onTogglePlayer = {
                currentQuarterState = currentQuarterState.copy(showPlayer = !currentQuarterState.showPlayer)
            },
            onQuarterChange = { newQuarter ->
                val prevQuarter = selectedQuarter
                selectedQuarter = newQuarter
                currentQuarterState = videoUiState.quarterList.find {
                    it.quarterNumber == newQuarter.filter { it.isDigit() }.toIntOrNull()
                } ?: QuarterVideoData(
                    quarterNumber = newQuarter.filter { it.isDigit() }.toIntOrNull() ?: 1
                )
                editingHighlight = HighlightUiState()
                Log.d("MatchVideoTab", "🔄 쿼터 변경: $prevQuarter → $newQuarter")
            },
            onExpandedChange = { expanded = it },
            onAddClick = {
                editingHighlight = HighlightUiState()
                showAddSheet = true
            },
            onEditClick = {
                editingHighlight = it
                showEditSheet = true
            },
            onDeleteVideo = {
                val videoId = currentQuarterState.videoId
                if (videoId > 0) {
                    deleteVideoId = videoId
                    showDeleteVideoDialog = true
                } else {
                    currentQuarterState = QuarterVideoData(
                        quarterNumber = selectedQuarter.filter { it.isDigit() }.toIntOrNull() ?: 1
                    )
                    Log.d("MatchVideoTab", "🔄 쿼터 유지: $selectedQuarter")
                }
            },
            onUploadClick = { launcher.launch("video/*") },
            onHighlightClick = { timestamp ->
                if (!currentQuarterState.showPlayer) {
                    currentQuarterState = currentQuarterState.copy(showPlayer = true)
                }
                Log.d("MatchVideoTab", "🔍 하이라이트 클릭: $timestamp 지점으로 이동")
                videoViewModel.seekToTimestamp(timestamp)
            }
        )
    }

    // 하이라이트 추가/수정 동작 처리
    val confirmAction: () -> Unit = {
        val updatedHighlight = editingHighlight.copy(
            startMin = editingHighlight.startMin.padStart(2, '0'),
            startSec = editingHighlight.startSec.padStart(2, '0'),
            endMin = editingHighlight.endMin.padStart(2, '0'),
            endSec = editingHighlight.endSec.padStart(2, '0')
        )

        // UI의 mm:ss 형식을 API 요청용 HH:mm:ss 형식으로 변환
        val startTime = if (updatedHighlight.startMin.contains(":")) {
            "00:${updatedHighlight.startMin}"
        } else {
            "00:${updatedHighlight.startMin}:${updatedHighlight.startSec}"
        }

        val endTime = if (updatedHighlight.endMin.contains(":")) {
            "00:${updatedHighlight.endMin}"
        } else {
            "00:${updatedHighlight.endMin}:${updatedHighlight.endSec}"
        }

        coroutineScope.launch {
            if (showAddSheet && currentQuarterState.videoId > 0) {
                Log.d("MatchVideoTab", "🎯 하이라이트 추가 시작")
                Log.d("MatchVideoTab", "📋 현재 쿼터: $selectedQuarter")
                Log.d("MatchVideoTab", "📋 비디오 ID: ${currentQuarterState.videoId}")
                Log.d("MatchVideoTab", "📋 하이라이트 제목: ${updatedHighlight.title}")
                Log.d("MatchVideoTab", "📋 시작 시간: $startTime")
                Log.d("MatchVideoTab", "📋 종료 시간: $endTime")

                val request = HighlightAddRequest(
                    videoId = currentQuarterState.videoId,
                    highlightName = updatedHighlight.title,
                    startTime = startTime,
                    endTime = endTime
                )

                try {
                    videoViewModel.addHighlight(request, matchId)
                    coroutineScope.launch { addSheetState.hide() }
                    showAddSheet = false
                    Log.d("MatchVideoTab", "✅ 하이라이트 추가 요청 완료")
                    Toast.makeText(context, "하이라이트가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("MatchVideoTab", "❌ 하이라이트 추가 실패", e)
                    Toast.makeText(context, "하이라이트 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else if (showEditSheet && updatedHighlight.id.isNotEmpty()) {
                Log.d("MatchVideoTab", "✏️ 하이라이트 수정 시작")
                Log.d("MatchVideoTab", "📋 하이라이트 ID: ${updatedHighlight.id}")
                Log.d("MatchVideoTab", "📋 수정된 제목: ${updatedHighlight.title}")
                Log.d("MatchVideoTab", "📋 수정된 시작 시간: $startTime")
                Log.d("MatchVideoTab", "📋 수정된 종료 시간: $endTime")

                val request = HighlightUpdateRequest(
                    highlightId = updatedHighlight.id.toInt(),
                    highlightName = updatedHighlight.title,
                    startTime = startTime,
                    endTime = endTime
                )

                try {
                    videoViewModel.updateHighlight(request, matchId)
                    coroutineScope.launch { editSheetState.hide() }
                    showEditSheet = false
                    Log.d("MatchVideoTab", "✅ 하이라이트 수정 요청 완료")
                    Toast.makeText(context, "하이라이트가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("MatchVideoTab", "❌ 하이라이트 수정 실패", e)
                    Toast.makeText(context, "하이라이트 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 하이라이트 추가 바텀시트
    if (showAddSheet) {
        HighlightBottomSheet(
            title = "하이라이트 구간 추가",
            sheetState = addSheetState,
            highlightState = editingHighlight,
            onStateChange = { editingHighlight = it },
            onDismiss = {
                coroutineScope.launch { addSheetState.hide() }
                showAddSheet = false
            },
            onConfirm = confirmAction,
            videoUri = currentQuarterState.videoUrl.takeIf { it.isNotBlank() }?.let { Uri.parse(it) }
        )
    }

    // 하이라이트 수정 바텀시트
    if (showEditSheet) {
        HighlightBottomSheet(
            title = "하이라이트 구간 수정",
            sheetState = editSheetState,
            highlightState = editingHighlight,
            onStateChange = { editingHighlight = it },
            onDismiss = {
                coroutineScope.launch { editSheetState.hide() }
                showEditSheet = false
            },
            onConfirm = confirmAction,
            onDelete = {
                Log.d("MatchVideoTab", "🗑️ 하이라이트 삭제 다이얼로그 표시")
                showDeleteDialog = true
            },
            videoUri = currentQuarterState.videoUrl.takeIf { it.isNotBlank() }?.let { Uri.parse(it) },
            confirmButtonText = "저장하기"
        )
    }

    // 하이라이트 삭제 확인 다이얼로그
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("하이라이트 삭제") },
            text = { Text("정말로 삭제하시겠어요?") },
            confirmButton = {
                TextButton(onClick = {
                    Log.d("MatchVideoTab", "🗑️ 하이라이트 삭제 시작")
                    Log.d("MatchVideoTab", "📋 하이라이트 ID: ${editingHighlight.id}")
                    coroutineScope.launch {
                        videoViewModel.deleteHighlight(editingHighlight.id.toInt(), matchId)
                        videoViewModel.getMatchVideos(matchId)
                        // 바텀시트 숨김
                        coroutineScope.launch { editSheetState.hide() }
                        showEditSheet = false
                        showDeleteDialog = false
                        Toast.makeText(context, "하이라이트가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    // 영상 삭제 확인 다이얼로그
    if (showDeleteVideoDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteVideoDialog = false },
            title = { Text("영상 삭제") },
            text = { Text("${selectedQuarter}의 영상을 정말로 삭제하시겠습니까? 되돌릴 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = {
                    Log.d("MatchVideoTab", "🗑️ 쿼터 영상 삭제 시작")
                    Log.d("MatchVideoTab", "📋 영상 ID: $deleteVideoId")
                    videoViewModel.deleteVideo(deleteVideoId, matchId)
                    showDeleteVideoDialog = false
                    Toast.makeText(context, "${selectedQuarter}의 영상이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteVideoDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MatchVideoTabPreview() {
    BallogTheme {
        MatchVideoTab(matchId = 29)
    }
}
