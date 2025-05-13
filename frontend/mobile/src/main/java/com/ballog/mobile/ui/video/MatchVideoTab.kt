package com.ballog.mobile.ui.video

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ballog.mobile.ui.theme.BallogTheme
import kotlinx.coroutines.launch

data class QuarterVideoData(
    val videoUri: Uri? = null,
    val highlights: List<HighlightUiState> = emptyList(),
    val showPlayer: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchVideoTab() {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedQuarter by remember { mutableStateOf("1 쿼터") }
    var expanded by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var editingHighlight by remember { mutableStateOf(HighlightUiState("", "", "", "", "")) }

    val quarterList = listOf("1 쿼터", "2 쿼터", "3 쿼터", "4 쿼터")

    val quarterData = remember {
        mutableStateMapOf<String, QuarterVideoData>().apply {
            quarterList.forEach { this[it] = QuarterVideoData() }
        }
    }

    fun currentData(): QuarterVideoData = quarterData[selectedQuarter] ?: QuarterVideoData()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            quarterData.forEach { (key, value) ->
                quarterData[key] = value.copy(showPlayer = false)
            }
            val currentQuarter = selectedQuarter
            quarterData[currentQuarter] = QuarterVideoData(
                videoUri = it,
                showPlayer = true,
                highlights = quarterData[currentQuarter]?.highlights ?: emptyList()
            )
            println("=== 영상 업로드 후 상태 ===")
            quarterData.forEach { (quarter, data) ->
                println("$quarter: videoUri=${data.videoUri}, showPlayer=${data.showPlayer}")
            }
        }
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
            expanded = expanded,
            onQuarterChange = {
                val prevQuarter = selectedQuarter
                selectedQuarter = it
                
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
        MatchVideoTab()
    }
}
