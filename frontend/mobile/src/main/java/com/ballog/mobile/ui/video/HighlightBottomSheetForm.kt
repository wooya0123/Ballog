package com.ballog.mobile.ui.video

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.ui.components.Input
import com.ballog.mobile.ui.theme.pretendard

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
            onValueChange = { if (it.length <= 2) onHourChange(it) },
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
            onValueChange = { if (it.length <= 2) onMinuteChange(it) },
            placeholder = "00",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
    }
}
