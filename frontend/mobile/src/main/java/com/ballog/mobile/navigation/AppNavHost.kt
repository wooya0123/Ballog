package com.ballog.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ballog.mobile.ui.auth.LoginScreen
import com.ballog.mobile.ui.auth.OnboardingScreen
import com.ballog.mobile.ui.auth.SignupBirthScreen
import com.ballog.mobile.ui.auth.SignupNicknameScreen
import com.ballog.mobile.ui.auth.SignupProfileScreen
import com.ballog.mobile.ui.auth.SignupScreen
import com.ballog.mobile.ui.auth.SignupVerificationScreen
import com.ballog.mobile.ui.main.MainScreen
import com.ballog.mobile.ui.profile.LikedVideosScreen
import com.ballog.mobile.ui.profile.MyPageScreen
import com.ballog.mobile.ui.profile.ProfileEditScreen
import com.ballog.mobile.ui.team.TeamCreateScreen
import com.ballog.mobile.ui.team.TeamDelegateScreen
import com.ballog.mobile.ui.team.TeamDetailScreen
import com.ballog.mobile.ui.team.TeamKickScreen
import com.ballog.mobile.ui.team.TeamListScreen
import com.ballog.mobile.viewmodel.AuthViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Routes.ONBOARDING
) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }
        composable(Routes.SIGNUP) {
            SignupScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }
        composable(Routes.SIGNUP_EMAIL_VERIFICATION) {
            SignupVerificationScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }
        composable(Routes.SIGNUP_NICKNAME) {
            SignupNicknameScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }
        composable(Routes.SIGNUP_BIRTHDAY) {
            SignupBirthScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }
        composable(Routes.SIGNUP_PROFILE_IMAGE) {
            SignupProfileScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }
        composable(
            route = "${Routes.MAIN}?teamId={teamId}",
            arguments = listOf(
                navArgument("teamId") { 
                    type = NavType.StringType 
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId")
            MainScreen(
                navController = navController,
                viewModel = authViewModel,
                initialTeamId = teamId?.toIntOrNull()
            )
        }
        composable(Routes.ONBOARDING) {
            // 앱 첫 실행 시 보여지는 온보딩 화면
            OnboardingScreen(
                onEmailLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Routes.SIGNUP}/{email}/{password}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // 이메일, 비밀번호를 받아 회원가입 시작
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""
            SignupScreen(email, password, navController)
        }

        composable(
            route = Routes.SIGNUP_EMAIL_VERIFICATION,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            // 이메일 인증 화면
            val email = backStackEntry.arguments?.getString("email") ?: ""
            SignupVerificationScreen(navController, email)
        }

        composable(Routes.SIGNUP_NICKNAME) {
            // 닉네임 설정 화면
            SignupNicknameScreen(navController)
        }

        composable(Routes.SIGNUP_BIRTHDAY) {
            // 생년월일 입력 화면
            SignupBirthScreen(navController)
        }

        composable(Routes.SIGNUP_PROFILE_IMAGE) {
            // 프로필 이미지 선택 화면
            SignupProfileScreen(navController)
        }

        // ─── 메인 홈 화면 ─────────────────────────────────────
        composable(Routes.MAIN) {
            // 하단 탭이 포함된 메인 화면
            MainScreen(navController)
        }

        // ─── 마이페이지 관련 화면 ─────────────────────────────
        composable(Routes.MYPAGE) {
            // 마이페이지 진입 화면
            MyPageScreen(navController)
        }

        composable(Routes.PROFILE_EDIT) {
            // 프로필 정보 수정 화면
            ProfileEditScreen(navController)
        }

        composable(Routes.MYPAGE_LIKED_VIDEOS) {
            // 좋아요한 영상 목록 화면
            LikedVideosScreen()
        }

        // ─── 팀 관련 화면 ───────────────────────────────────
        composable(Routes.TEAM_LIST) {
            // 팀 리스트 화면
            TeamListScreen(navController)
        }
        composable(Routes.TEAM_CREATE) {
            // 팀 생성 화면
            TeamCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onClose = { navController.popBackStack() }
            )
        }
        composable(
            route = "${Routes.TEAM_DETAIL}/{teamId}",
            arguments = listOf(
                navArgument("teamId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
            TeamDetailScreen(
                navController = navController,
                teamId = teamId
            )
        }
        composable(
            route = Routes.TEAM_SETTINGS,
            arguments = listOf(navArgument("teamName") { type = NavType.StringType })
        ) { backStackEntry ->
            // 팀 설정 화면
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamSettingScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = {
                    navController.popBackStack()
                },
                onDelegateClick = {
                    navController.navigate("team/delegate/$teamName")
                },
                onKickMemberClick = {
                    navController.navigate("team/kick/$teamName")
                },
                onInviteLinkClick = {
                    // TODO: 팀 초대 링크 다이얼로그
                },
                onDeleteTeamClick = {
                    // TODO: 팀 삭제 확인 다이얼로그
                },
                onLeaveTeamClick = {
                    // TODO: 팀 탈퇴 확인 다이얼로그
                }
            )
        }
        composable(
            route = Routes.TEAM_DELEGATE,
            arguments = listOf(navArgument("teamName") { type = NavType.StringType })
        ) { backStackEntry ->
            // 팀 권한 위임 화면
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamDelegateScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onSaveClick = {
                    // TODO: 위임 저장 처리
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.TEAM_KICK,
            arguments = listOf(navArgument("teamName") { type = NavType.StringType })
        ) { backStackEntry ->
            // 팀 멤버 강제퇴장 화면
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamKickScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onSaveClick = {
                    // TODO: 강퇴 저장 처리
                    navController.popBackStack()
                }
            )
        }
    }
}
