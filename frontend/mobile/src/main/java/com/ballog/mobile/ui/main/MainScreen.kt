package com.ballog.mobile.ui.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ballog.mobile.ui.components.NavigationBar
import com.ballog.mobile.ui.home.HomeScreen
import com.ballog.mobile.ui.match.MatchScreen
import com.ballog.mobile.ui.team.TeamListScreen
import com.ballog.mobile.ui.team.TeamDetailScreen
import com.ballog.mobile.ui.team.TeamSettingScreen
import com.ballog.mobile.ui.team.TeamDelegateScreen
import com.ballog.mobile.ui.team.TeamKickScreen
import com.ballog.mobile.ui.team.TeamCreateScreen
import com.ballog.mobile.ui.profile.MyPageScreen
import androidx.compose.material3.Scaffold
import com.ballog.mobile.ui.components.NavigationTab
import com.ballog.mobile.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ballog.mobile.ui.match.MatchRegisterMode
import com.ballog.mobile.ui.match.MatchRegisterScreen
import com.ballog.mobile.viewmodel.MatchViewModel
import com.ballog.mobile.viewmodel.TeamViewModel
import java.time.LocalDate
import com.ballog.mobile.ui.match.MatchDataScreen
import com.ballog.mobile.navigation.Routes

private const val TAG = "MainScreen"

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    initialTeamId: Int? = null
) {
    var selectedTab by remember { mutableStateOf(if (initialTeamId != null) NavigationTab.TEAM else NavigationTab.HOME) }
    val teamNavController = rememberNavController()
    val teamViewModel: TeamViewModel = viewModel()

    // 초기 팀 ID가 있으면 팀 상세 화면으로 자동 이동
    LaunchedEffect(initialTeamId) {
        if (initialTeamId != null) {
            // 팀 목록 화면의 백스택 항목 생성
            teamNavController.navigate("team_list")

            // 팀 상세 화면으로 이동
            teamNavController.navigate("team_detail/$initialTeamId")

            // 로그 출력
            Log.d(TAG, "팀 상세 화면으로 자동 이동: teamId=$initialTeamId")
        }
    }

    Scaffold(
//        bottomBar = {
//            Text("네비게이션 바") // NavigationBar 임시 제거
//        }
        bottomBar = {
            NavigationBar(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                },
                onActionClick = {
                    // 중앙 버튼 눌렀을 때 동작
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                NavigationTab.HOME -> HomeScreen()
                NavigationTab.MATCH -> MatchTabScreen(navController = rememberNavController())
                NavigationTab.TEAM -> TeamTabScreen(teamNavController, teamViewModel)
                NavigationTab.MYPAGE -> MyPageScreen(navController)
                NavigationTab.DATA -> MatchDataScreen()
            }
        }
    }
}

@Composable
fun TeamTabScreen(
    teamNavController: NavHostController,
    teamViewModel: TeamViewModel
) {
    // 팀 관련 화면에 대한 중첩 네비게이션
    NavHost(
        navController = teamNavController,
        startDestination = "team_list"
    ) {
        composable("team_list") {
            Log.d(TAG, "팀 목록 화면 표시")
            TeamListScreen(navController = teamNavController, viewModel = teamViewModel)
        }

        // 팀 생성 화면 추가
        composable("team/create") {
            Log.d(TAG, "팀 생성 화면 표시")
            TeamCreateScreen(
                teamViewModel = teamViewModel,
                onNavigateBack = {
                    teamNavController.popBackStack()
                },
                onClose = {
                    teamNavController.popBackStack()
                }
            )
        }

        composable(
            route = "team_detail/{teamId}",
            arguments = listOf(
                navArgument("teamId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
            Log.d(TAG, "팀 상세 화면 표시: teamId=$teamId")
            TeamDetailScreen(
                navController = teamNavController,
                teamId = teamId,
                viewModel = teamViewModel
            )
        }
        // 팀 설정 화면 추가
        composable(
            route = "team/settings/{teamName}",
            arguments = listOf(
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            Log.d(TAG, "팀 설정 화면 표시: teamName=$teamName")
            TeamSettingScreen(
                navController = teamNavController,
                teamName = teamName,
                viewModel = teamViewModel,
                onBackClick = {
                    teamNavController.popBackStack()
                },
                onDelegateClick = {
                    teamNavController.navigate("team/delegate/$teamName")
                },
                onKickMemberClick = {
                    teamNavController.navigate("team/kick/$teamName")
                },
                onInviteLinkClick = {
                    // 초대 링크 다이얼로그는 TeamSettingScreen 내부에서 처리됩니다
                    Log.d(TAG, "팀 초대 링크 생성 요청")
                },
                onDeleteTeamClick = {
                    // 팀 삭제 요청
                    Log.d(TAG, "팀 삭제 요청")
                    // TODO: 팀 삭제 후 팀 목록으로 이동
                    teamNavController.popBackStack()
                },
                onLeaveTeamClick = {
                    // 팀 탈퇴 요청
                    Log.d(TAG, "팀 탈퇴 요청")
                    // TODO: 팀 탈퇴 후 팀 목록으로 이동
                    teamNavController.popBackStack()
                }
            )
        }
        // 권한 위임 화면 추가
        composable(
            route = "team/delegate/{teamName}",
            arguments = listOf(
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            Log.d(TAG, "팀 권한 위임 화면 표시: teamName=$teamName")
            TeamDelegateScreen(
                navController = teamNavController,
                teamName = teamName,
                viewModel = teamViewModel,
                onBackClick = {
                    teamNavController.popBackStack()
                },
                onCloseClick = {
                    teamNavController.popBackStack()
                }
            )
        }
        // 멤버 강제 퇴장 화면 추가
        composable(
            route = "team/kick/{teamName}",
            arguments = listOf(
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            Log.d(TAG, "팀 멤버 강제 퇴장 화면 표시: teamName=$teamName")
            TeamKickScreen(
                navController = teamNavController,
                teamName = teamName,
                viewModel = teamViewModel,
                onBackClick = {
                    teamNavController.popBackStack()
                },
                onCloseClick = {
                    teamNavController.popBackStack()
                }
            )
        }
        // 팀 매치 등록 화면 추가
        composable(
            route = "match/register/{date}?teamId={teamId}",
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("teamId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            val teamId = backStackEntry.arguments?.getInt("teamId")?.takeIf { it != -1 }
            val matchViewModel: MatchViewModel = viewModel()

            MatchRegisterScreen(
                mode = if (teamId != null) MatchRegisterMode.TEAM else MatchRegisterMode.PERSONAL,
                navController = teamNavController,
                viewModel = matchViewModel,
                selectedDate = selectedDate,
                teamId = teamId
            )
        }
    }
}

@Composable
fun MatchTabScreen(navController: NavHostController) {
    val matchViewModel: MatchViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "match/main"
    ) {
        composable("match/main") {
            MatchScreen(navController = navController, viewModel = matchViewModel)
        }

        composable(
            route = "match/register/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()

            MatchRegisterScreen(
                mode = MatchRegisterMode.PERSONAL,
                navController = navController,
                viewModel = matchViewModel,
                selectedDate = selectedDate
            )
        }
    }
}

