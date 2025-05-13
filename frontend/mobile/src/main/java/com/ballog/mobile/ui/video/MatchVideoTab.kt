package com.ballog.mobile.ui.video

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ballog.mobile.ui.theme.BallogTheme
import kotlinx.coroutines.launch
import com.ballog.mobile.util.FileUtils
import com.ballog.mobile.util.VideoUtils
import com.ballog.mobile.viewmodel.VideoViewModel
import android.util.Log
import com.ballog.mobile.ui.video.QuarterVideoData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchVideoTab(matchId: Int, matchName: String) {
    Log.d("MatchVideoTab", "🟦 $matchId 번 $matchName 매치의 영상 탭 접속")

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedQuarter by remember { mutableStateOf("1 쿼터") }
    var expanded by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var editingHighlight by remember { mutableStateOf(HighlightUiState("", "", "", "", "")) }
    val videoViewModel: VideoViewModel = viewModel()
    val videoUiState by videoViewModel.videoUiState.collectAsState()

    val quarterOptions = remember(videoUiState.totalQuarters) {
        (1..videoUiState.totalQuarters).map { "$it 쿼터" }
    }

    val context = LocalContext.current


    val quarterData = remember(quarterOptions) {
        mutableStateMapOf<String, QuarterVideoData>().apply {
            quarterOptions.forEach { this[it] = QuarterVideoData() }
        }
    }

    fun currentData(): QuarterVideoData = quarterData[selectedQuarter] ?: QuarterVideoData()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            Log.d("MatchVideoTab", "📁 영상 URI 선택됨: $uri")

            // 1. 쿼터 상태 갱신 (showPlayer false 처리)
            Log.d("MatchVideoTab", "🔄 모든 쿼터의 showPlayer = false 설정")
            quarterData.forEach { (key, value) ->
                quarterData[key] = value.copy(showPlayer = false)
            }

            // 2. 쿼터 UI 상태 먼저 반영
            val currentQuarter = selectedQuarter
            Log.d("MatchVideoTab", "🎞️ 선택된 쿼터: $currentQuarter")
            quarterData[currentQuarter] = QuarterVideoData(
                videoUri = it,
                showPlayer = true,
                highlights = quarterData[currentQuarter]?.highlights ?: emptyList()
            )

            // 3. 업로드를 위한 File 및 duration 추출
            val file = FileUtils.uriToFile(context, it)
            val duration = VideoUtils.getVideoDurationString(context, it)
            val quarterNumber = selectedQuarter.filter { it.isDigit() }.toIntOrNull() ?: 1

            Log.d("MatchVideoTab", "📦 File name: ${file.name}, duration: $duration, quarter: $quarterNumber, matchId: $matchId")

            // 4. presigned URL 요청 + S3 업로드 진행
            Log.d("MatchVideoTab", "🚀 영상 업로드 API 호출 시작")

            videoViewModel.uploadQuarterVideo(
                context = context,
                file = file,
                matchId = matchId,
                quarterNumber = quarterNumber,
                duration = duration
            )

            // 5. 디버깅 로그
            Log.d("MatchVideoTab", "✅ 업로드 후 쿼터 상태 확인")
            quarterData.forEach { (quarter, data) ->
                Log.d("MatchVideoTab", "$quarter: videoUri=${data.videoUri}, showPlayer=${data.showPlayer}")
            }
        } ?: Log.w("MatchVideoTab", "⛔ 영상 URI가 null입니다. 선택 취소되었을 수 있음")
    }


    Column(modifier = Modifier.fillMaxSize()) {
        val current = currentData()

        HighlightContentSection(
            videoUri = current.videoUri,
            highlights = current.highlights,
            showPlayer = current.showPlayer,
            onTogglePlayer = {
                quarterData[selectedQuarter] = current.copy(showPlayer = !current.showPlayer)
            },
            selectedQuarter = selectedQuarter,
            quarterOptions = quarterOptions,
            expanded = expanded,
            onQuarterChange = {
                val prevQuarter = selectedQuarter
                selectedQuarter = it

                Log.d("MatchVideoTab", "🔄 쿼터 변경됨: 이전 : $prevQuarter, 현재 : $selectedQuarter")
                
                if (prevQuarter.isNotEmpty() && quarterData.containsKey(prevQuarter)) {
                    val prevData = quarterData[prevQuarter]
                    if (prevData != null) {
                        quarterData[prevQuarter] = prevData.copy(showPlayer = false)
                    }
                }
                
                if (it.isNotEmpty() && quarterData.containsKey(it)) {
                    val newQuarterData = quarterData[it]
                    if (newQuarterData != null && newQuarterData.videoUri != null) {
                        quarterData[it] = newQuarterData.copy(showPlayer = true)
                    }
                }
                
                println("=== 쿼터 변경 후 상태 ===")
                println("이전 쿼터: $prevQuarter, 현재 쿼터: $it")
                quarterData.forEach { (quarter, data) ->
                    println("$quarter: videoUri=${data.videoUri}, showPlayer=${data.showPlayer}")
                }
            },
            onExpandedChange = { expanded = it },
            onAddClick = { showAddSheet = true },
            onEditClick = {
                editingHighlight = it
                showEditSheet = true
            },
            onDeleteVideo = {
                quarterData[selectedQuarter] = QuarterVideoData()
            },
            onUploadClick = {
                launcher.launch("video/*")
            }
        )
    }

    val confirmAction: () -> Unit = {
        val current = currentData()
        val updatedHighlight = editingHighlight.copy(
            startHour = editingHighlight.startHour.padStart(2, '0'),
            startMin = editingHighlight.startMin.padStart(2, '0'),
            endHour = editingHighlight.endHour.padStart(2, '0'),
            endMin = editingHighlight.endMin.padStart(2, '0')
        )
        editingHighlight = updatedHighlight
        val updatedList = when {
            showAddSheet -> current.highlights + updatedHighlight
            showEditSheet -> current.highlights.map {
                if (it == editingHighlight) updatedHighlight else it
            }
            else -> current.highlights
        }
        quarterData[selectedQuarter] = current.copy(highlights = updatedList)

        coroutineScope.launch {
            sheetState.hide()
            showAddSheet = false
            showEditSheet = false
        }
    }

    if (showAddSheet) {
        HighlightBottomSheet(
            title = "하이라이트 구간 추가",
            sheetState = sheetState,
            highlightState = editingHighlight,
            onStateChange = { editingHighlight = it },
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    showAddSheet = false
                }
            },
            onConfirm = confirmAction,
            videoUri = currentData().videoUri
        )
    }

    if (showEditSheet) {
        HighlightBottomSheet(
            title = "하이라이트 구간 수정",
            sheetState = sheetState,
            highlightState = editingHighlight,
            onStateChange = { editingHighlight = it },
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    showEditSheet = false
                }
            },
            onConfirm = confirmAction,
            onDelete = {
                val current = currentData()
                val updatedList = current.highlights.filterNot { it == editingHighlight }
                quarterData[selectedQuarter] = current.copy(highlights = updatedList)
                coroutineScope.launch {
                    sheetState.hide()
                    showEditSheet = false
                }
            },
            videoUri = currentData().videoUri,
            confirmButtonText = "저장하기"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MatchVideoTabPreview() {
    BallogTheme {
        MatchVideoTab(matchId = 29, matchName = "친선매치")
    }
}
