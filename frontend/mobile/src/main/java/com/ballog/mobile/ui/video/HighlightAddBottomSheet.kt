package com.ballog.mobile.ui.video

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ballog.mobile.ui.components.BallogBottomSheet
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.theme.BallogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightAddBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (HighlightUiState) -> Unit
) {
    var highlightState by remember {
        mutableStateOf(HighlightUiState("", "", "", "", ""))
    }

    BallogBottomSheet(
        title = "하이라이트 구간 추가",
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        HighlightForm(
            state = highlightState,
            onStateChange = { highlightState = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        BallogButton(
            onClick = { onConfirm(highlightState) },
            label = "추가하기",
            buttonColor = ButtonColor.BLACK,
            type = ButtonType.LABEL_ONLY,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightAddBottomSheetPreview() {
    BallogTheme {
        HighlightAddBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismiss = {},
            onConfirm = {}
        )
    }
}
