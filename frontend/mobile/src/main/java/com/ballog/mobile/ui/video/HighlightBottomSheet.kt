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
            // 저장할 때 패딩 처리
            // MM:SS 형식으로 저장된 경우 처리
            val startTime = if (highlightState.startMin.contains(":")) {
                val parts = highlightState.startMin.split(":")
                val min = parts.getOrNull(0)?.padStart(2, '0') ?: "00"
                val sec = parts.getOrNull(1)?.padStart(2, '0') ?: "00"
                "$min:$sec"
            } else {
                // 개별 필드로 저장된 경우
                val min = highlightState.startMin.ifEmpty { "0" }.padStart(2, '0')
                val sec = highlightState.startSec.ifEmpty { "0" }.padStart(2, '0')
                "$min:$sec"
            }
            
            val endTime = if (highlightState.endMin.contains(":")) {
                val parts = highlightState.endMin.split(":")
                val min = parts.getOrNull(0)?.padStart(2, '0') ?: "00"
                val sec = parts.getOrNull(1)?.padStart(2, '0') ?: "00"
                "$min:$sec"
            } else {
                // 개별 필드로 저장된 경우
                val min = highlightState.endMin.ifEmpty { "0" }.padStart(2, '0')
                val sec = highlightState.endSec.ifEmpty { "0" }.padStart(2, '0')
                "$min:$sec"
            }
            
            val padded = highlightState.copy(
                startMin = startTime,
                startSec = "",
                endMin = endTime,
                endSec = ""
            )

            // 시간 유효성 검사 - 시작 시간이 종료 시간보다 작은지만 확인
            val isValid = try {
                val startParts = startTime.split(":")
                val endParts = endTime.split(":")
                
                val startSec = startParts[0].toInt() * 60 + startParts[1].toInt()
                val endSec = endParts[0].toInt() * 60 + endParts[1].toInt()
                
                startSec < endSec
            } catch (e: Exception) {
                false
            }

            if (isValid) {
                onStateChange(padded)
                onConfirm()
            } else {
                Toast.makeText(context, "시작 시간은 종료 시간보다 작아야 합니다.", Toast.LENGTH_SHORT).show()
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

        // 시간 값 처리 - MM:SS 형식 또는 개별 필드
        val startMinValue: String
        val startSecValue: String
        
        if (state.startMin.contains(":")) {
            // MM:SS 형식으로 저장된 경우
            val parts = state.startMin.split(":")
            startMinValue = parts.getOrNull(0) ?: ""
            startSecValue = parts.getOrNull(1) ?: ""
        } else {
            // 개별 필드로 저장된 경우
            startMinValue = state.startMin
            startSecValue = state.startSec
        }
        
        val endMinValue: String
        val endSecValue: String
        
        if (state.endMin.contains(":")) {
            // MM:SS 형식으로 저장된 경우
            val parts = state.endMin.split(":")
            endMinValue = parts.getOrNull(0) ?: ""
            endSecValue = parts.getOrNull(1) ?: ""
        } else {
            // 개별 필드로 저장된 경우
            endMinValue = state.endMin
            endSecValue = state.endSec
        }

        FormFieldWithLabel("시작 지점") {
            TimeInputFields(
                min = startMinValue,
                onMinChange = { 
                    if (state.startMin.contains(":")) {
                        // 분:초 형식으로 저장된 경우
                        onStateChange(state.copy(startMin = "$it:$startSecValue"))
                    } else {
                        // 기존 방식
                        onStateChange(state.copy(startMin = it)) 
                    }
                },
                sec = startSecValue,
                onSecChange = { 
                    if (state.startMin.contains(":")) {
                        // 분:초 형식으로 저장된 경우
                        onStateChange(state.copy(startMin = "$startMinValue:$it"))
                    } else {
                        // 기존 방식
                        onStateChange(state.copy(startSec = it)) 
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        FormFieldWithLabel("종료 지점") {
            TimeInputFields(
                min = endMinValue,
                onMinChange = { 
                    if (state.endMin.contains(":")) {
                        // 분:초 형식으로 저장된 경우
                        onStateChange(state.copy(endMin = "$it:$endSecValue"))
                    } else {
                        // 기존 방식
                        onStateChange(state.copy(endMin = it)) 
                    }
                },
                sec = endSecValue,
                onSecChange = { 
                    if (state.endMin.contains(":")) {
                        // 분:초 형식으로 저장된 경우
                        onStateChange(state.copy(endMin = "$endMinValue:$it"))
                    } else {
                        // 기존 방식
                        onStateChange(state.copy(endSec = it)) 
                    }
                }
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
    min: String,
    onMinChange: (String) -> Unit,
    sec: String,
    onSecChange: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 분(minutes) 입력 필드
        Input(
            value = min,
            onValueChange = { newValue -> 
                if (newValue.length <= 2 && newValue.all { c -> c.isDigit() }) {
                    onMinChange(newValue)
                }
            },
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
        // 초(seconds) 입력 필드
        Input(
            value = sec,
            onValueChange = { newValue -> 
                if (newValue.length <= 2 && newValue.all { c -> c.isDigit() }) {
                    onSecChange(newValue)
                }
            },
            placeholder = "00",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
    }
}
