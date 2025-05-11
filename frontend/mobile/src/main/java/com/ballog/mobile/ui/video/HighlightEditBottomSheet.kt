package com.ballog.mobile.ui.video

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ballog.mobile.R
import com.ballog.mobile.ui.components.BallogBottomSheet
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightEditBottomSheet(
    sheetState: SheetState,
    initialState: HighlightUiState,
    onDismiss: () -> Unit,
    onConfirm: (HighlightUiState) -> Unit,
    onDelete: () -> Unit
) {
    var highlightState by remember { mutableStateOf(initialState) }

    BallogBottomSheet(
        title = "하이라이트 구간 수정",
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        HighlightForm(
            state = highlightState,
            onStateChange = { highlightState = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        BottomActionButtons(
            onConfirm = { onConfirm(highlightState) },
            onDelete = onDelete
        )
    }
}

@Composable
private fun BottomActionButtons(
    onConfirm: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp), // 👈 공통 높이 고정
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BallogButton(
            onClick = onConfirm,
            label = "저장하기",
            buttonColor = ButtonColor.BLACK,
            type = ButtonType.LABEL_ONLY,
            modifier = Modifier
                .weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        BallogButton(
            onClick = onDelete,
            icon = painterResource(id = R.drawable.ic_trash),
            buttonColor = ButtonColor.ALERT,
            type = ButtonType.ICON_ONLY,
            modifier = Modifier
                .size(height = 48.dp, width = 48.dp) // 👈 높이 명시적으로 맞춤
        )
    }
}
