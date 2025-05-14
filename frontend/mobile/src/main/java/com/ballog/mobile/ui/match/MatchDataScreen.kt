package com.ballog.mobile.ui.match

import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import com.ballog.mobile.data.model.MatchDataCardInfo
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ballog.mobile.data.service.MatchReportService
import com.ballog.mobile.ui.theme.System
import kotlinx.coroutines.launch
import com.ballog.mobile.ui.components.NavigationTab

@Composable
fun MatchDataScreen(
    navController: NavHostController,
    matchTabNavController: NavHostController,
    setSelectedTab: (NavigationTab) -> Unit,
    viewModel: MatchViewModel = viewModel(),
    matchReportService: MatchReportService
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var matchData by remember { mutableStateOf<List<MatchDataCardInfo>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val quarterReportList by matchReportService.quarterReportList.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 최초 진입 시 SharedPreferences 값만 확인
    LaunchedEffect(Unit) {
        Log.d("MatchDataScreen", "uiState: $uiState")
        viewModel.checkFieldCorners()
        println(quarterReportList)
    }

    // MatchUiState가 Loading일 때만 데이터 로드
    LaunchedEffect(uiState) {
        if (uiState is MatchUiState.Loading) {
                scope.launch {
                    try {
                        matchReportService.createMatchReport()
                    } catch (e: Exception) {
                        viewModel.setNoData()
                    }
                }

        }
        if(uiState is MatchUiState.Success) {
            val mapped = quarterReportList.map { exercise ->
                MatchDataCardInfo(
                    id = exercise.id,
                    date = exercise.date,
                    startTime = exercise.gameReportData.startTime,
                    endTime = exercise.gameReportData.endTime,
                    buttonText = if(exercise.gameSide == null || exercise.quarterNumber == null) "정보 입력하기" else "정보 수정하기"
                )
            }
            matchData = mapped
            viewModel.setSuccess(matchData)
        }
    }

    // quarterReportList 값이 변경될 때만 matchData와 viewModel 상태를 갱신
    LaunchedEffect(quarterReportList) {
        if (uiState is MatchUiState.Loading) {
            if (quarterReportList.isEmpty()) {
                viewModel.setNoData()
            } else {
                val mapped = quarterReportList.map { exercise ->
                    MatchDataCardInfo(
                        id = exercise.id,
                        date = exercise.date,
                        startTime = exercise.gameReportData.startTime,
                        endTime = exercise.gameReportData.endTime,
                        buttonText = if(exercise.gameSide == null || exercise.quarterNumber == null) "정보 입력하기" else "정보 수정하기"
                    )
                }
                matchData = mapped
                viewModel.setSuccess(matchData)
            }
        }
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
            val cardList = quarterReportList.map { exercise ->
                MatchDataCardInfo(
                    id = exercise.id,
                    date = exercise.date,
                    startTime = exercise.gameReportData.startTime,
                    endTime = exercise.gameReportData.endTime,
                    buttonText = if(exercise.gameSide == null || exercise.quarterNumber == null) "정보 입력하기" else "정보 수정하기"
                )
            }
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
                            id = card.id,
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
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = System.Red,
                        fontSize = 12.sp,
                        fontFamily = pretendard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, bottom = 8.dp)
                    )
                }
                BallogButton(
                    onClick = {
                        scope.launch {
                            var flag = false;
                            quarterReportList.map { quarter ->
                                if (quarter.quarterNumber == null || quarter.gameSide == null) {
                                    flag = true
                                }
                            }
                            if (flag) {
                                errorMessage = "입력하지 않은 쿼터 정보가 있습니다."
                            } else {
                                val matchReportResponse = matchReportService.sendMatchReport()
                                if (matchReportResponse == null) {
                                    errorMessage = "매치 데이터 전송에 실패하였습니다."
                                } else {
                                    setSelectedTab(NavigationTab.MATCH)
                                    matchTabNavController.navigate("match/detail/${matchReportResponse.matchId}/${matchReportResponse.matchName}")
                                }
                            }
                        }
                    },
                    type = ButtonType.LABEL_ONLY,
                    buttonColor = ButtonColor.BLACK,
                    label = "저장",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (showModal && modalData != null) {
                MatchDataModal(
                    data = modalData!!,
                    matchReportService = matchReportService,
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
    matchReportService: MatchReportService,
    onDismiss: () -> Unit
) {
    val quarterReportList by matchReportService.quarterReportList.collectAsState()
    var quarter by remember { mutableStateOf("") }
    var selectedSide by remember { mutableStateOf<String>("left") } // "LEFT" or "RIGHT"
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        quarterReportList.map { quarterReport ->
            if(data.id == quarterReport.id) {
                if (quarterReport.quarterNumber != null)
                    quarter = quarterReport.quarterNumber.toString()
                if(quarterReport.gameSide != null)
                    selectedSide = quarterReport.gameSide
            }
        }
    }
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
                .height(520.dp)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
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
                            heatData = matchReportService.getHeatMapData(data.id),
                            modifier = Modifier.matchParentSize()
                        )
                        Row(Modifier.matchParentSize()) {
                            // 왼쪽 영역
                            Box(
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { selectedSide = "left" }
                            ) {
                                if (selectedSide == "left") {
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
                                    .clickable { selectedSide = "right" }
                            ) {
                                if (selectedSide == "right") {
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
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = System.Red,
                            fontSize = 12.sp,
                            fontFamily = pretendard,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }
                    BallogButton(
                        onClick = { 
                            if(quarter == ""){
                                errorMessage = "쿼터 번호를 입력해주세요"
                            }
                            else {
                                matchReportService.updateNumberAndSide(
                                    id = data.id,
                                    quarterNumber = quarter.toInt(),
                                    side = selectedSide
                                )
                                onDismiss()
                            }},
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
