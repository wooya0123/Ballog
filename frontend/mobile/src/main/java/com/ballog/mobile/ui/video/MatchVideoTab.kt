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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchVideoTab(matchId: Int, totalQuarters: Int) {
    Log.d("MatchVideoTab", "🟦 $matchId 번 매치의 영상 탭 접속")

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedQuarter by remember { mutableStateOf("1 쿼터") }
    var expanded by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var editingHighlight by remember { mutableStateOf(HighlightUiState()) }
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

    LaunchedEffect(Unit) {
        videoViewModel.getMatchVideos(matchId)
    }

    LaunchedEffect(videoUiState.quarterList) {
        Log.d("MatchVideoTab", "🧩 API 응답 기반으로 quarterData 초기화")
        videoUiState.quarterList.forEach { video ->
            val quarter = "${video.quarterNumber ?: 1} 쿼터"
            quarterData[quarter] = QuarterVideoData(
                videoId = video.videoId ?: -1,
                quarterNumber = video.quarterNumber ?: 1,
                videoUrl = video.videoUrl ?: "",
                highlights = video.highlights,
                showPlayer = false
            )
            Log.d("MatchVideoTab", "🧩 $quarter → videoUrl=${video.videoUrl}, highlight=${video.highlights.size}개")
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            Log.d("MatchVideoTab", "📁 영상 URI 선택됨: $uri")

            // 모든 쿼터의 showPlayer false
            quarterData.forEach { (key, value) ->
                quarterData[key] = value.copy(showPlayer = false)
            }

            val currentQuarter = selectedQuarter
            val file = FileUtils.uriToFile(context, it)
            val duration = VideoUtils.getVideoDurationString(context, it)
            val quarterNumber = currentQuarter.filter { it.isDigit() }.toIntOrNull() ?: 1

            // 임시 Uri로 반영
            quarterData[currentQuarter] = QuarterVideoData(
                videoId = -1,
                quarterNumber = quarterNumber,
                videoUrl = it.toString(),
                showPlayer = true,
                highlights = quarterData[currentQuarter]?.highlights ?: emptyList()
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

    Column(modifier = Modifier.fillMaxSize()) {
        val current = currentData()

        HighlightContentSection(
            videoUri = current.videoUrl.takeIf { it.isNotBlank() }?.let { Uri.parse(it) },
            highlights = current.highlights,
            showPlayer = current.showPlayer,
            selectedQuarter = selectedQuarter,
            quarterOptions = quarterOptions,
            expanded = expanded,
            onTogglePlayer = {
                quarterData[selectedQuarter] = current.copy(showPlayer = !current.showPlayer)
            },
            onQuarterChange = {
                val prevQuarter = selectedQuarter
                selectedQuarter = it

                quarterData[prevQuarter] = quarterData[prevQuarter]?.copy(showPlayer = false) ?: QuarterVideoData()
                quarterData[it] = quarterData[it]?.copy(showPlayer = true) ?: QuarterVideoData()

                Log.d("MatchVideoTab", "🔄 쿼터 변경: $prevQuarter → $it")
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
        val updatedHighlight = editingHighlight.copy(
            startMin = editingHighlight.startMin.padStart(2, '0'),
            startSec = editingHighlight.startSec.padStart(2, '0'),
            endMin = editingHighlight.endMin.padStart(2, '0'),
            endSec = editingHighlight.endSec.padStart(2, '0')
        )
        val current = currentData()
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
                coroutineScope.launch { sheetState.hide(); showAddSheet = false }
            },
            onConfirm = confirmAction,
            videoUri = currentData().videoUrl.let(Uri::parse)
        )
    }

    if (showEditSheet) {
        HighlightBottomSheet(
            title = "하이라이트 구간 수정",
            sheetState = sheetState,
            highlightState = editingHighlight,
            onStateChange = { editingHighlight = it },
            onDismiss = {
                coroutineScope.launch { sheetState.hide(); showEditSheet = false }
            },
            onConfirm = confirmAction,
            onDelete = {
                val current = currentData()
                val updatedList = current.highlights.filterNot { it == editingHighlight }
                quarterData[selectedQuarter] = current.copy(highlights = updatedList)
                coroutineScope.launch {
                    sheetState.hide(); showEditSheet = false
                }
            },
            videoUri = currentData().videoUrl.let(Uri::parse),
            confirmButtonText = "저장하기"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MatchVideoTabPreview() {
    BallogTheme {
        MatchVideoTab(matchId = 29, totalQuarters = 4)
    }
}
