package com.ballog.mobile.ui.match

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ballog.mobile.ui.video.MatchVideoTab

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

    // 화면이 처음 표시될 때 매치 리스트(혹은 단일 매치) 요청
    LaunchedEffect(matchId) {
        // NOTE: 단일 매치 API가 없으므로, 임시로 이번달 매치 전체를 불러와서 id로 찾음
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
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error ?: "알 수 없는 오류가 발생했습니다",
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
                    1 -> MatchVideoTab(matchId = matchId)
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

