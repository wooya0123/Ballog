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
fun VideoTab() {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedQuarter by remember { mutableStateOf("1 쿼터") }
    var expanded by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var editingHighlight by remember { mutableStateOf(HighlightUiState("", "", "", "", "")) }

    // TODO: API 응답으로 대체 예정
    val quarterList = listOf("1 쿼터", "2 쿼터", "3 쿼터", "4 쿼터")

    val quarterData = remember {
        mutableStateMapOf<String, QuarterVideoData>().apply {
            quarterList.forEach { this[it] = QuarterVideoData() }
        }
    }

    // ❗ 현재 쿼터의 최신 데이터 접근
    fun currentData(): QuarterVideoData {
        return quarterData[selectedQuarter] ?: QuarterVideoData()
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // 모든 showPlayer false → 선택 쿼터만 true
            quarterData.forEach { (key, value) ->
                quarterData[key] = value.copy(showPlayer = false)
            }
            quarterData[selectedQuarter] = currentData().copy(
                videoUri = it,
                showPlayer = true
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HighlightContentSection(
            videoUri = currentData().videoUri,
            highlights = currentData().highlights,
            showPlayer = currentData().showPlayer,
            onTogglePlayer = {
                quarterData[selectedQuarter] = currentData().copy(showPlayer = !currentData().showPlayer)
            },
            selectedQuarter = selectedQuarter,
            expanded = expanded,
            onQuarterChange = {
                selectedQuarter = it
                quarterData.forEach { (key, data) ->
                    quarterData[key] = data.copy(showPlayer = false)
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

    if (showAddSheet) {
        HighlightAddBottomSheet(
            sheetState = sheetState,
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    showAddSheet = false
                }
            },
            onConfirm = { newHighlight ->
                val updatedHighlights = currentData().highlights + newHighlight
                quarterData[selectedQuarter] = currentData().copy(highlights = updatedHighlights)
                coroutineScope.launch {
                    sheetState.hide()
                    showAddSheet = false
                }
            }
        )
    }

    if (showEditSheet) {
        HighlightEditBottomSheet(
            sheetState = sheetState,
            initialState = editingHighlight,
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    showEditSheet = false
                }
            },
            onConfirm = { updated ->
                val updatedList = currentData().highlights.map {
                    if (it == editingHighlight) updated else it
                }
                quarterData[selectedQuarter] = currentData().copy(highlights = updatedList)
                coroutineScope.launch {
                    sheetState.hide()
                    showEditSheet = false
                }
            },
            onDelete = {
                val updatedList = currentData().highlights.filterNot { it == editingHighlight }
                quarterData[selectedQuarter] = currentData().copy(highlights = updatedList)
                coroutineScope.launch {
                    sheetState.hide()
                    showEditSheet = false
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VideoTabPreview() {
    BallogTheme {
        VideoTab()
    }
}
