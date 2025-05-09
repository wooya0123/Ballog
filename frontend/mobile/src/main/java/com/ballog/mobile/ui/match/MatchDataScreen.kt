package com.ballog.mobile.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.ballog.mobile.ui.theme.pretendard
import androidx.compose.ui.tooling.preview.Preview
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Immutable
import com.ballog.mobile.ui.components.Input
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.painterResource
import com.ballog.mobile.R

@Composable
fun MatchDataScreen() {
    var showModal by remember { mutableStateOf(false) }
    var modalData by remember { mutableStateOf<MatchDataCardInfo?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Navigation Bar
        TopNavItem(
            title = "매치 데이터 연동",
            type = TopNavType.MAIN_BASIC
        )
        Spacer(modifier = Modifier.height(20.dp))
        // MatchDataCard 리스트 (샘플 데이터)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val cardList = listOf(
                MatchDataCardInfo("2025.05.08", "15:37", "15:50", "정보 수정하기"),
                MatchDataCardInfo("2025.05.09", "16:00", "16:45", "정보 입력하기"),
                MatchDataCardInfo("2025.05.10", "17:10", "17:55", "정보 입력하기"),
                MatchDataCardInfo("2025.05.11", "18:20", "19:05", "정보 입력하기")
            )
            cardList.forEach { card ->
                MatchDataCard(
                    date = card.date,
                    startTime = card.startTime,
                    endTime = card.endTime,
                    buttonText = card.buttonText,
                    onButtonClick = {
                        modalData = card
                        showModal = true
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        // 저장하기 버튼 (하단)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(48.dp)
                .background(Color(0xFF1B1B1D), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "저장하기",
                fontSize = 16.sp,
                color = Color(0xFF7EE4EA),
                fontWeight = FontWeight.SemiBold,
                fontFamily = pretendard
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
    if (showModal && modalData != null) {
        MatchDataModal(
            data = modalData!!,
            onDismiss = { showModal = false }
        )
    }
}

@Immutable
data class MatchDataCardInfo(
    val date: String,
    val startTime: String,
    val endTime: String,
    val buttonText: String
)

@Composable
fun MatchDataModal(
    data: MatchDataCardInfo,
    onDismiss: () -> Unit
) {
    var quarter by remember { mutableStateOf("") }
    var heatData by remember { mutableStateOf(List(15) { List(10) { (0..5).random() } }) }
    var selectedSide by remember { mutableStateOf<String?>("LEFT") } // "LEFT" or "RIGHT"

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        Box(
            Modifier
                .align(Alignment.Center)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .background(Gray.Gray100)
                .width(310.dp)
                .height(480.dp)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "쿼터 정보를 입력해주세요",
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Gray.Gray800,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
                // 쿼터 입력
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "몇번째 쿼터인가요 ?",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Gray.Gray800,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Input(
                        value = quarter,
                        onValueChange = { quarter = it },
                        placeholder = "쿼터 숫자",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.height(24.dp))
                // 진영 선택
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "경기 진영을 선택해주세요 !",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Gray.Gray800,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    // 진영 선택 HeatMap + Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(168.dp)
                    ) {
                        HeatMap(
                            heatData = heatData,
                            modifier = Modifier.matchParentSize()
                        )
                        Row(Modifier.matchParentSize()) {
                            // 왼쪽 영역
                            Box(
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { selectedSide = "LEFT" }
                            ) {
                                if (selectedSide == "LEFT") {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .background(Gray.Gray700.copy(alpha = 0.85f))
                                    )
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_team),
                                        contentDescription = "왼쪽 선택됨",
                                        tint = Primary,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                            // 오른쪽 영역
                            Box(
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { selectedSide = "RIGHT" }
                            ) {
                                if (selectedSide == "RIGHT") {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .background(Gray.Gray700.copy(alpha = 0.98f))
                                    )
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_team),
                                        contentDescription = "오른쪽 선택됨",
                                        tint = Primary,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                // 저장 버튼
                BallogButton(
                    onClick = { /* TODO: 저장 동작 구현 */ },
                    type = ButtonType.LABEL_ONLY,
                    buttonColor = ButtonColor.BLACK,
                    label = "저장",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewMatchDataModal() {
    MatchDataModal(
        data = MatchDataCardInfo("2025.05.08", "15:37", "15:50", "정보 수정하기"),
        onDismiss = {}
    )
}

@Preview
@Composable
fun PreviewMatchDataScreen() {
    MatchDataScreen()
} 
