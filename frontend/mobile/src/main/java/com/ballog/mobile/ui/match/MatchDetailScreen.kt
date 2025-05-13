package com.ballog.mobile.ui.match

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.TabMenu
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.pretendard
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ballog.mobile.viewmodel.MatchViewModel
import com.ballog.mobile.data.model.MatchState
import com.ballog.mobile.ui.video.VideoTab
import com.ballog.mobile.data.service.SamsungHealthDataService
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.HealthDataService
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataTypes
import androidx.compose.ui.platform.LocalContext

private const val TAG = "MatchDetailScreen"

@Composable
fun MatchDetailScreen(
    navController: NavController,
    matchId: Int,
    viewModel: MatchViewModel = viewModel()
) {
    Log.d(TAG, "MatchDetailScreen 시작: matchId=$matchId")

    var selectedTab by remember { mutableIntStateOf(0) }
    val matchState by viewModel.matchState.collectAsState()
    val error = when (matchState) {
        is MatchState.Error -> (matchState as MatchState.Error).message
        else -> null
    }
    val isLoading = matchState is MatchState.Loading
    val match = (matchState as? MatchState.Success)?.matches?.find { it.id == matchId }

    // 삼성 헬스 데이터 관련 상태
    var healthDataStore by remember { mutableStateOf<HealthDataStore?>(null) }
    var samsungHealthDataService by remember { mutableStateOf<SamsungHealthDataService?>(null) }
    var isHealthDataLoading by remember { mutableStateOf(false) }
    var healthDataError by remember { mutableStateOf<String?>(null) }
    
    // 컨텍스트 저장
    val context = LocalContext.current

    // 삼성 헬스 초기화
    LaunchedEffect(Unit) {
        try {
            healthDataStore = HealthDataService.getStore(context)
            samsungHealthDataService = SamsungHealthDataService(context)
            Log.d(TAG, "Samsung Health 초기화 성공")
        } catch (e: Exception) {
            Log.e(TAG, "Samsung Health 초기화 실패: ${e.message}")
            healthDataError = "삼성 헬스 초기화 실패: ${e.message}"
        }
    }

    // 권한 요청 및 데이터 로드
    LaunchedEffect(healthDataStore, samsungHealthDataService) {
        if (healthDataStore != null && samsungHealthDataService != null) {
            isHealthDataLoading = true
            try {
                // TODO: 권한 요청 부분 - 추후 제거 가능
                val permissions = setOf(
                    Permission.of(DataTypes.EXERCISE, AccessType.READ),
                    Permission.of(DataTypes.HEART_RATE, AccessType.READ),
                    Permission.of(DataTypes.EXERCISE_LOCATION, AccessType.READ)
                )

                val grantedPermissions = healthDataStore?.requestPermissions(
                    permissions,
                    context as Activity
                )

                if (grantedPermissions?.containsAll(permissions) == true) {
                    // TODO: 권한 요청 부분 끝
                    val exerciseData = samsungHealthDataService?.getExercise()
                    Log.d(TAG, "운동 데이터 로딩 완료: ${exerciseData?.size ?: 0}개")
                } else {
                    healthDataError = "필요한 권한이 부여되지 않았습니다"
                }
            } catch (e: Exception) {
                Log.e(TAG, "데이터 로딩 실패: ${e.message}")
                healthDataError = "데이터 로딩 실패: ${e.message}"
            } finally {
                isHealthDataLoading = false
            }
        }
    }

    // 화면이 처음 표시될 때 매치 리스트 요청
    LaunchedEffect(matchId) {
        val month = java.time.LocalDate.now().toString().substring(0, 7) // yyyy-MM
        viewModel.fetchMyMatches(month)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        TopNavItem(
            title = match?.matchName ?: "매치 상세",
            type = TopNavType.DETAIL_WITH_BACK,
            onBackClick = { navController.popBackStack() },
        )
        TabMenu(
            leftTabText = "레포트",
            rightTabText = "영상",
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        when {
            isLoading || isHealthDataLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null || healthDataError != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error ?: healthDataError ?: "알 수 없는 오류가 발생했습니다",
                        fontSize = 16.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Medium,
                        color = Gray.Gray500,
                        textAlign = TextAlign.Center
                    )
                }
            }
            match != null -> {
                when (selectedTab) {
                    0 -> MatchReportTab(match = match)
                    1 -> MatchVideoTab()
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "매치 정보를 불러올 수 없습니다",
                        fontSize = 16.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Medium,
                        color = Gray.Gray500,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

