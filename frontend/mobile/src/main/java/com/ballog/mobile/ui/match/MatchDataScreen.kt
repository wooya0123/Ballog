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
import com.ballog.mobile.data.service.SamsungHealthDataService
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import android.widget.Toast
import android.util.Log
import com.ballog.mobile.BallogApplication
import com.ballog.mobile.data.api.RetrofitInstance.matchApi
import com.ballog.mobile.data.model.Exercise
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import com.ballog.mobile.data.service.MatchReportService
import com.ballog.mobile.ui.components.HeatMapWithSideSelection
import com.ballog.mobile.data.service.ExerciseMetricsCalculator


private const val TAG = "MatchDataScreen"

@Composable
fun MatchDataScreen() {
    var showModal by remember { mutableStateOf(false) }
    var modalData by remember { mutableStateOf<MatchDataCardInfo?>(null) }
    var modalHeatData by remember { mutableStateOf<List<List<Int>>?>(null) }
    val context = LocalContext.current
    val samsungHealthService = remember { SamsungHealthDataService(context) }
    val matchReportService = remember {
        MatchReportService(
            context = context,
            matchApi = matchApi,
            samsungHealthDataService = samsungHealthService
        )
    }
    val scope = rememberCoroutineScope()
    var matchData by remember { mutableStateOf<List<MatchDataCardInfo>>(emptyList()) }
    var exerciseDataMap by remember { mutableStateOf<Map<String, Exercise>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 선택된 데이터들의 쿼터 정보를 저장할 맵
    var selectedDataMap by remember { mutableStateOf<Map<String, QuarterInfo>>(emptyMap()) }

    // 데이터 로드
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                // 매번 새로운 데이터 로드
                val exerciseData = samsungHealthService.getExercise()
                matchData = exerciseData.map { exercise ->
                    MatchDataCardInfo(
                        id = exercise.id,
                        date = exercise.date,
                        startTime = exercise.startTime,
                        endTime = exercise.endTime,
                        buttonText = "정보 입력하기"
                    )
                }
                // Exercise 데이터를 Map으로 저장 (새로 계산된 히트맵 포함)
                exerciseDataMap = exerciseData.associateBy { it.id }
            } catch (e: Exception) {
                Log.e(TAG, "데이터 로드 실패: ${e.message}")
                errorMessage = "데이터 로드 실패: ${e.message}"
            } finally {
                isLoading = false
            }
        }
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
            matchData.forEach { data ->
                MatchDataCard(
                    date = data.date,
                    startTime = data.startTime,
                    endTime = data.endTime,
                    buttonText = data.buttonText,
                    onButtonClick = {
                        modalData = data
                        modalHeatData = exerciseDataMap[data.id]?.heatmapData
                        showModal = true
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(48.dp)
                .background(Color(0xFF1B1B1D), shape = RoundedCornerShape(8.dp))
                .clickable {
                    if (selectedDataMap.isEmpty()) {
                        Toast.makeText(context, "선택된 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                        return@clickable
                    }

                    scope.launch {
                        try {
                            isLoading = true
                            errorMessage = null

                            // 토큰 가져오기
                            val token = BallogApplication.getInstance().tokenManager.getAccessToken().firstOrNull()
                                ?: throw Exception("토큰이 없습니다.")

                            // 현재 날짜를 yyyy-MM-dd 형식으로 변환
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val matchDate = dateFormat.format(Date())

                            // 쿼터 데이터 생성
                            val quarterData = selectedDataMap.map { (id, quarterInfo) ->
                                // 선택된 데이터의 실제 시작/종료 시간 사용
                                val selectedData = matchData.find { it.id == id }
                                    ?: throw Exception("선택된 데이터를 찾을 수 없습니다.")

                                MatchReportService.QuarterData(
                                    quarterNumber = quarterInfo.quarter.toInt(),
                                    gameSide = quarterInfo.side,
                                    startTime = selectedData.startTime,
                                    endTime = selectedData.endTime
                                )
                            }

                            // 리포트 전송
                            val success = matchReportService.createAndSendMatchReport(
                                matchDate = matchDate,
                                quarterData = quarterData,
                                token = token
                            )

                            if (success) {
                                // 데이터 목록에서 해당 항목 제거
                                matchData = matchData.filter { it.id !in selectedDataMap.keys }
                                // 선택된 데이터 맵 초기화
                                selectedDataMap = emptyMap()
                                Toast.makeText(context, "데이터가 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                throw Exception("서버 전송 실패")
                            }
                        } catch (e: Exception) {
                            Log.e("MatchDataScreen", "데이터 전송 실패: ${e.message}")
                            errorMessage = "데이터 전송 실패: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
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
            heatData = modalHeatData ?: List(10) { List(16) { 0 } },
            onDismiss = { showModal = false },
            onSave = { quarter, side ->
                selectedDataMap = selectedDataMap + (modalData!!.id to QuarterInfo(quarter, side))
                showModal = false
                Toast.makeText(context, "데이터가 선택되었습니다. 저장하기 버튼을 눌러 전송해주세요.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 에러 메시지 표시
    errorMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        errorMessage = null
    }
}

@Immutable
data class MatchDataCardInfo(
    val id: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val buttonText: String
)

// 쿼터 정보를 저장하는 데이터 클래스
data class QuarterInfo(
    val quarter: String,
    val side: String
)


@Composable
fun MatchDataModal(
    data: MatchDataCardInfo,
    heatData: List<List<Int>>,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var quarter by remember { mutableStateOf("") }
    var selectedSide by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

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
                    HeatMapWithSideSelection(
                        heatData = heatData,
                        selectedSide = selectedSide,
                        onSideSelect = { selectedSide = it }
                    )
                }
                Spacer(Modifier.height(24.dp))
                // 저장 버튼
                BallogButton(
                    onClick = {
                        if (quarter.isBlank()) {
                            Toast.makeText(context, "쿼터 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                            return@BallogButton
                        }
                        if (selectedSide == null) {
                            Toast.makeText(context, "진영을 선택해주세요", Toast.LENGTH_SHORT).show()
                            return@BallogButton
                        }

                        onSave(quarter, selectedSide!!)
                    },
                    type = ButtonType.LABEL_ONLY,
                    buttonColor = ButtonColor.PRIMARY,
                    label = if (isLoading) "전송 중..." else "저장",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        }
    }
}
