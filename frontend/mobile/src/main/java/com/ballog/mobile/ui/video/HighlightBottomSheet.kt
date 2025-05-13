package com.ballog.mobile.ui.video

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.R
import com.ballog.mobile.ui.components.*
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.pretendard
import com.ballog.mobile.util.getVideoDurationInSec
import com.ballog.mobile.util.isValidHighlightTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightBottomSheet(
    title: String,
    sheetState: SheetState,
    highlightState: HighlightUiState,
    onStateChange: (HighlightUiState) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDelete: (() -> Unit)? = null,
    videoUri: Uri?,
    confirmButtonText: String = "저장하기"
) {
    val context = LocalContext.current
    val videoDurationSec = remember(videoUri) {
        if (videoUri != null) getVideoDurationInSec(context, videoUri) else 0
    }

    BallogBottomSheet(
        title = title,
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        HighlightForm(
            state = highlightState,
            onStateChange = onStateChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        val confirmAction = {
            val padded = highlightState.copy(
                startHour = highlightState.startHour.padStart(2, '0'),
                startMin = highlightState.startMin.padStart(2, '0'),
                endHour = highlightState.endHour.padStart(2, '0'),
                endMin = highlightState.endMin.padStart(2, '0')
            )

            val isValid = isValidHighlightTime(
                padded.startHour, padded.startMin,
                padded.endHour, padded.endMin,
                videoDurationSec
            )

            if (isValid) {
                onStateChange(padded)
                onConfirm()
            } else {
                Toast.makeText(context, "시간 입력이 잘못되었거나 영상 길이를 초과했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        if (onDelete == null) {
            BallogButton(
                onClick = confirmAction,
                label = confirmButtonText,
                buttonColor = ButtonColor.BLACK,
                type = ButtonType.LABEL_ONLY,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BallogButton(
                    onClick = confirmAction,
                    label = confirmButtonText,
                    buttonColor = ButtonColor.BLACK,
                    type = ButtonType.LABEL_ONLY,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                BallogButton(
                    onClick = onDelete,
                    icon = painterResource(id = R.drawable.ic_trash),
                    buttonColor = ButtonColor.ALERT,
                    type = ButtonType.ICON_ONLY,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun HighlightForm(
    state: HighlightUiState,
    onStateChange: (HighlightUiState) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        FormFieldWithLabel("하이라이트 제목") {
            Input(
                value = state.title,
                onValueChange = { onStateChange(state.copy(title = it)) },
                placeholder = "하이라이트 구간의 제목을 입력해주세요."
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        FormFieldWithLabel("시작 지점") {
            TimeInputFields(
                hour = state.startHour,
                onHourChange = { onStateChange(state.copy(startHour = it)) },
                minute = state.startMin,
                onMinuteChange = { onStateChange(state.copy(startMin = it)) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        FormFieldWithLabel("종료 지점") {
            TimeInputFields(
                hour = state.endHour,
                onHourChange = { onStateChange(state.copy(endHour = it)) },
                minute = state.endMin,
                onMinuteChange = { onStateChange(state.copy(endMin = it)) }
            )
        }
    }
}

@Composable
private fun FormFieldWithLabel(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun TimeInputFields(
    hour: String,
    onHourChange: (String) -> Unit,
    minute: String,
    onMinuteChange: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Input(
            value = hour,
            onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) onHourChange(it) },
            placeholder = "00",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
        Text(
            ":",
            style = TextStyle(
                fontFamily = pretendard,
                color = Color.Gray,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        )
        Input(
            value = minute,
            onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) onMinuteChange(it) },
            placeholder = "00",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
    }
}
