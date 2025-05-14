package com.ballog.mobile.ui.team

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ballog.mobile.R
import com.ballog.mobile.data.model.Player
import com.ballog.mobile.data.model.TeamDetail
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.TeamPlayerCard
import com.ballog.mobile.ui.components.TabMenu
import com.ballog.mobile.ui.components.TeamInfoCard
import com.ballog.mobile.ui.components.TeamStats
import com.ballog.mobile.ui.home.PlayerCardDialog
import com.ballog.mobile.ui.match.TeamMatchTab
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.pretendard
import com.ballog.mobile.viewmodel.TeamViewModel

private const val TAG = "TeamDetailScreen"

@Composable
fun TeamDetailScreen(
    navController: NavController,
    teamId: Int,
    viewModel: TeamViewModel
) {
    Log.d(TAG, "TeamDetailScreen 시작: teamId=$teamId")

    var selectedTab by remember { mutableIntStateOf(0) }
    val teamDetail by viewModel.teamDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 선수카드 보기를 위한 변수
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }

    Log.d(TAG, "상태 - isLoading: $isLoading, error: $error, teamDetail: ${teamDetail != null}")

    // 화면이 처음 표시될 때 팀 상세 정보 요청
    LaunchedEffect(teamId) {
        Log.d(TAG, "LaunchedEffect 실행: teamId=$teamId")
        try {
            viewModel.getTeamDetail(teamId)
            Log.d(TAG, "getTeamDetail 호출 완료")
        } catch (e: Exception) {
            Log.e(TAG, "getTeamDetail 호출 중 오류 발생", e)
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        when {
            isLoading -> {
                Log.d(TAG, "로딩 중 UI 표시")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Log.d(TAG, "에러 UI 표시: $error")
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
            teamDetail != null -> {
                Log.d(TAG, "teamDetail UI 표시 시작")
                teamDetail?.let { detail ->
                    Log.d(TAG, "팀 정보: 이름=${detail.name}, 인원=${detail.players.size}")
                    TopNavItem(
                        title = detail.name,
                        type = TopNavType.DETAIL_WITH_BACK_SETTINGS,
                        onBackClick = {
                            Log.d(TAG, "뒤로가기 클릭")
                            navController.popBackStack()
                        },
                        onActionClick = {
                            Log.d(TAG, "설정 클릭")
                            // Navigate to TeamSettingScreen with teamId
                            Log.d(TAG, "팀 설정 화면으로 이동 - teamId: $teamId")

                            // 중첩된 NavHost에 맞는 경로 사용
                            val settingsRoute = "team/settings/$teamId"
                            Log.d(TAG, "팀 설정 화면으로 이동: $settingsRoute")
                            navController.navigate(settingsRoute)
                        }
                    )

                    TabMenu(
                        leftTabText = "팀 정보",
                        rightTabText = "매치",
                        selectedTab = selectedTab,
                        onTabSelected = {
                            Log.d(TAG, "탭 선택: $it")
                            selectedTab = it
                        }
                    )

                    when (selectedTab) {
                        0 -> {
                            Log.d(TAG, "팀 정보 탭 표시")
                            TeamInfoTab(
                                teamDetail = detail,
                                selectedPlayer = selectedPlayer,
                                onDismiss = { selectedPlayer = null },
                                onPlayerSelected = { selectedPlayer = it }
                            )
                        }
                        1 -> {
                            Log.d(TAG, "매치 탭 표시")
                            TeamMatchTab(navController = navController, teamId = teamId)
                        }
                    }
                }
            }
            else -> {
                Log.d(TAG, "데이터 없음 UI 표시")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "팀 정보를 불러올 수 없습니다",
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

    selectedPlayer?.let { p ->
        PlayerCardDialog(
            name = p.nickname,
            imageUrl = p.cardImageUrl,
            stats = listOf(
                "Speed" to p.stats.speed.toString(),
                "Stamina" to p.stats.stamina.toString(),
                "Attack" to p.stats.attack.toString(),
                "Defense" to p.stats.defense.toString(),
                "Recovery" to p.stats.recovery.toString()
            ),
            onDismiss = { selectedPlayer = null }
        )
    }

    // 화면이 종료될 때 실행되는 DisposableEffect
    DisposableEffect(Unit) {
        Log.d(TAG, "TeamDetailScreen 진입")
        onDispose {
            Log.d(TAG, "TeamDetailScreen 종료")
        }
    }
}

@Composable
private fun TeamInfoTab(
    teamDetail: TeamDetail,
    selectedPlayer: Player?,
    onDismiss: () -> Unit,
    onPlayerSelected: (Player) -> Unit
) {
    Log.d(TAG, "TeamInfoTab 시작")
    // 유효하지 않은 팀 데이터 확인
    if (teamDetail.name.isEmpty() && teamDetail.players.isEmpty()) {
        Log.e(TAG, "유효하지 않은 팀 데이터")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "팀 정보를 불러올 수 없습니다",
                fontSize = 16.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                color = Gray.Gray500,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                Log.d(TAG, "TeamInfoCard 표시")
                val stats = teamDetail.stats
                Log.d(TAG, "팀 스탯 처리: attack=${stats.attack}, defense=${stats.defense}, speed=${stats.speed}")

                TeamInfoCard(
                    stats = TeamStats(
                        attack = stats.attack,
                        defence = stats.defense,
                        speed = stats.speed,
                        recovery = stats.recovery,
                        stamina = stats.stamina
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Log.d(TAG, "멤버 수 아이콘 표시")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "멤버 수",
                        tint = Gray.Gray800,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = teamDetail.players.size.toString(),
                        fontSize = 12.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        color = Gray.Gray800
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (teamDetail.players.isNotEmpty()) {
                Log.d(TAG, "플레이어 목록 표시: ${teamDetail.players.size}명")
                items(teamDetail.players) { player ->
                    Log.d(TAG, "플레이어 카드 표시: ${player.nickname}")
                    TeamPlayerCard(
                        name = player.nickname,
                        isManager = player.role == "MANAGER",
                        modifier = Modifier.fillMaxWidth(),
                        onCardClick = {
                            Log.d("PlayerCard", "🔥 카드 클릭됨: ${player.nickname}")
                            onPlayerSelected(player)
                        }
                    )
                }
            } else {
                Log.d(TAG, "플레이어 없음 표시")
                item {
                    Text(
                        text = "팀원이 없습니다",
                        fontSize = 14.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Medium,
                        color = Gray.Gray500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

