package com.ballog.mobile.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.ui.theme.pretendard
import androidx.compose.ui.text.font.FontWeight
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.ButtonColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import com.ballog.mobile.ui.components.StatCard
import com.ballog.mobile.ui.match.HeatMap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import com.ballog.mobile.ui.components.PlayerCardFigma
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

/**
 * 홈 화면을 담당하는 Composable입니다.
 */
@Composable
fun HomeScreen() {
    var showPlayerCard by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray.Gray100)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            // 상단 인사말
            Text(
                text = "김볼록님, 안녕하세요!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Gray.Gray700,
                fontFamily = pretendard,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            // 선수 카드 보기 버튼
            BallogButton(
                onClick = { showPlayerCard = true },
                type = ButtonType.BOTH,
                buttonColor = ButtonColor.BLACK,
                icon = painterResource(id = com.ballog.mobile.R.drawable.ic_card),
                label = "선수 카드 보기",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            HeatMap(
                heatData = List(15) { List(10) { (0..5).random() } },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "통계",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray.Gray700,
                    fontFamily = pretendard
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "지난 5 경기의 추이를 나타냅니다",
                    fontSize = 12.sp,
                    color = Gray.Gray500,
                    fontFamily = pretendard
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // 통계 카드 4개 (이동거리, 평균속도, 스프린트, 평균심박) 2x2 그리드로 배치
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "이동거리",
                        value = "5.2km",
                        bars = listOf(false, false, true),
                        barColor = Primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "평균속도",
                        value = "7.2km/h",
                        bars = listOf(false, true),
                        barColor = Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "스프린트",
                        value = "12회",
                        bars = listOf(false, true, true),
                        barColor = Primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "평균심박",
                        value = "135bpm",
                        bars = listOf(true),
                        barColor = Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        // Box의 마지막에 조건부로 모달을 Overlay
        if (showPlayerCard) {
            PlayerCardDialog(onDismiss = { showPlayerCard = false })
        }
    }
}

@Composable
fun PlayerCardDialog(onDismiss: () -> Unit) {
    // 어두운 반투명 배경 + X 버튼은 전체 화면의 오른쪽 위에 고정
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.98f))
    ) {
        // X 버튼 (전체 화면 기준 오른쪽 위)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 24.dp)
        ) {
            androidx.compose.material3.IconButton(onClick = onDismiss) {
                androidx.compose.material3.Icon(
                    painter = painterResource(id = com.ballog.mobile.R.drawable.ic_close),
                    contentDescription = "닫기",
                    tint = Color.Unspecified
                )
            }
        }
        // 중앙에 카드 (Glow 효과 추가)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .drawBehind {
                    // RadialGradient를 활용한 그라데이션 Glow 효과
                    drawRoundRect(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.35f),
                                Primary.copy(alpha = 0.0f)
                            ),
                            center = center,
                            radius = size.maxDimension * 0.7f
                        ),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx())
                    )
                }
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(22.dp)
                )
        ) {
            PlayerCardFigma(
                name = "KIM GAHEE",
                stats = listOf(
                    "Speed" to "78",
                    "Stamina" to "80",
                    "Attack" to "64",
                    "Defense" to "80",
                    "Recovery" to "76"
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}

@Preview(showBackground = true)
@Composable
fun PreviewPlayerCardDialog() {
    PlayerCardDialog(onDismiss = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreenWithPlayerCard() {
    var showPlayerCard by remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen()
        if (showPlayerCard) {
            PlayerCardDialog(onDismiss = { showPlayerCard = false })
        }
    }
}

@Composable
fun HomeScreenWithPlayerCardPreview(showPlayerCard: Boolean, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "김볼록님, 안녕하세요!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Gray.Gray700,
                fontFamily = pretendard,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            BallogButton(
                onClick = {},
                type = ButtonType.BOTH,
                buttonColor = ButtonColor.BLACK,
                icon = painterResource(id = com.ballog.mobile.R.drawable.ic_card),
                label = "선수 카드 보기",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            HeatMap(
                heatData = List(15) { List(10) { (0..5).random() } },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "이동거리",
                        value = "5.2km",
                        bars = listOf(false, false, true),
                        barColor = Primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "평균속도",
                        value = "7.2km/h",
                        bars = listOf(false, true),
                        barColor = Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "스프린트",
                        value = "12회",
                        bars = listOf(false, true, true),
                        barColor = Primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "평균심박",
                        value = "135bpm",
                        bars = listOf(true),
                        barColor = Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        if (showPlayerCard) {
            PlayerCardDialog(onDismiss = onDismiss)
        }
    }
} 
