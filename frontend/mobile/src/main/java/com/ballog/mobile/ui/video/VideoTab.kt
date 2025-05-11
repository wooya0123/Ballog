package com.ballog.mobile.ui.video

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ballog.mobile.ui.theme.BallogTheme
import kotlinx.coroutines.launch

// ✅ 목업 하이라이트 데이터
val mockHighlights = listOf(
    HighlightUiState("하이라이트 1", "01", "20", "01", "35"),
    HighlightUiState("하이라이트 2", "03", "10", "03", "18"),
    HighlightUiState("하이라이트 3", "02", "50", "03", "05")
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
    var highlightState by remember { mutableStateOf(mockHighlights.first()) }

    val uploadedSet = remember { setOf("1 쿼터") }
    val isUploaded = selectedQuarter in uploadedSet

    Column(modifier = Modifier.fillMaxSize()) {
        HighlightContentSection(
            highlights = if (isUploaded) mockHighlights else emptyList(),
            selectedQuarter = selectedQuarter,
            expanded = expanded,
            onQuarterChange = { selectedQuarter = it },
            onExpandedChange = { expanded = it },
            onAddClick = { showAddSheet = true },
            onEditClick = { highlight ->
                highlightState = highlight
                showEditSheet = true
            },
            onDeleteVideo = { /* TODO: 영상 삭제 처리 */ }
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
            onConfirm = { newState ->
                // TODO: 구간 추가 처리
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
            initialState = highlightState,
            onDismiss = {
                coroutineScope.launch {
                    sheetState.hide()
                    showEditSheet = false
                }
            },
            onConfirm = { updated ->
                highlightState = updated
                // TODO: 구간 수정 처리
                coroutineScope.launch {
                    sheetState.hide()
                    showEditSheet = false
                }
            },
            onDelete = {
                // TODO: 구간 삭제 처리
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
