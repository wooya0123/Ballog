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
import com.ballog.mobile.ui.components.Input
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.painterResource
import com.ballog.mobile.R
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ballog.mobile.viewmodel.MatchViewModel
import com.ballog.mobile.viewmodel.MatchUiState
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.shape.CircleShape
import com.ballog.mobile.data.model.MatchDataCardInfo

@Composable
fun MatchDataScreen(viewModel: MatchViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // 최초 진입 시 SharedPreferences 값만 확인
    LaunchedEffect(Unit) {
        android.util.Log.d("MatchDataScreen", "uiState: $uiState")
        viewModel.checkFieldCorners()
    }

    when (uiState) {
        is MatchUiState.WaitingForStadiumData -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(10.dp))
                    WatchWithAnimatedCircles()
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "경기장 데이터를 기다리고 있습니다",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = pretendard,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "워치를 연동해주세요",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = pretendard,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
        is MatchUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "데이터 로딩 중입니다",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = pretendard,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        is MatchUiState.NoData -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopNavItem(
                    title = "매치 데이터 연동",
                    type = TopNavType.MAIN_BASIC
                )
                Spacer(modifier = Modifier.height(180.dp))
                Image(
                    painter = painterResource(id = R.drawable.samsung_health_144x144),
                    contentDescription = "삼성헬스 아이콘",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "삼성 헬스 데이터가 없습니다.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = pretendard,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "삼성 헬스에서 운동 데이터를 연동해 주세요.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard,
                    color = Color.Gray
                )
            }
        }
        is MatchUiState.Success -> {
            var showModal by remember { mutableStateOf(false) }
            var modalData by remember { mutableStateOf<MatchDataCardInfo?>(null) }
            val cardList = listOf(
                MatchDataCardInfo("2025.05.08", "15:37", "15:50", "정보 수정하기"),
                MatchDataCardInfo("2025.05.09", "16:00", "16:45", "정보 입력하기"),
                MatchDataCardInfo("2025.05.10", "17:10", "17:55", "정보 입력하기"),
                MatchDataCardInfo("2025.05.11", "18:20", "19:05", "정보 입력하기")
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                TopNavItem(
                    title = "매치 데이터 연동",
                    type = TopNavType.MAIN_BASIC
                )
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(48.dp)
                        .background(Color(0xFF1B1B1D), shape = RoundedCornerShape(8.dp)),
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
        is MatchUiState.Error -> {
            val message = (uiState as? MatchUiState.Error)?.message ?: "알 수 없는 에러"
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "에러 발생: $message",
                    color = Color.Red,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        else -> {
            // StadiumDataSuccess 등 미처리 상태 → 삼성헬스 데이터 없음 UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopNavItem(
                    title = "매치 데이터 연동",
                    type = TopNavType.MAIN_BASIC
                )
                Spacer(modifier = Modifier.height(180.dp))
                Image(
                    painter = painterResource(id = R.drawable.samsung_health_144x144),
                    contentDescription = "삼성헬스 아이콘",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "삼성 헬스 데이터가 없습니다.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = pretendard,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "삼성 헬스에서 운동 데이터를 연동해 주세요.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard,
                    color = Color.Gray
                )
            }
        }
    }
}

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
                                            .background(Gray.Gray700.copy(alpha = 0.85f))
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

@Composable
fun WatchWithAnimatedCircles() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val scale3 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .graphicsLayer(scaleX = scale3, scaleY = scale3)
                .background(
                    color = Primary.copy(alpha = 0.15f),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer(scaleX = scale2, scaleY = scale2)
                .background(
                    color = Primary.copy(alpha = 0.30f),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(140.dp)
                .graphicsLayer(scaleX = scale1, scaleY = scale1)
                .background(
                    color = Primary.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        )
        Image(
            painter = painterResource(id = R.drawable.ic_watch),
            contentDescription = "워치 아이콘",
            modifier = Modifier.size(110.dp)
        )
    }
} 
