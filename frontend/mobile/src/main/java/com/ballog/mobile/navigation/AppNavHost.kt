package com.ballog.mobile.navigation

import androidx.compose.runtime.Composable
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
import com.ballog.mobile.ui.team.TeamSettingScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    // 네비게이션 그래프 시작지점 설정
    NavHost(navController = navController, startDestination = Routes.ONBOARDING) {

        // ─── 온보딩 및 로그인/회원가입 ───────────────────────
        composable(Routes.ONBOARDING) {
            // 앱 첫 실행 시 보여지는 온보딩 화면
            OnboardingScreen(
                onEmailLoginClick = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.LOGIN) {
            // 이메일/비밀번호 로그인 화면
            LoginScreen(
                onLoginClick = {
                    // 로그인 성공 시 메인화면으로 이동 (로그인 화면은 백스택에서 제거)
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    // TODO: 비밀번호 찾기 화면 구현 예정
                },
                onSignUpNavigate = { email, password ->
                    // 회원가입 첫 화면으로 이동 (이메일, 비밀번호 전달)
                    navController.navigate("${Routes.SIGNUP}/$email/$password") {
                        popUpTo(Routes.LOGIN) { inclusive = true }
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
            route = Routes.TEAM_DETAIL,
            arguments = listOf(navArgument("teamName") { type = NavType.StringType })
        ) { backStackEntry ->
            // 팀 상세 정보 화면
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamDetailScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = { navController.popBackStack() },
                onSettingClick = {
                    navController.navigate("team/settings/$teamName")
                }
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
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onDelegateClick = { navController.navigate("team/delegate/$teamName") },
                onKickMemberClick = { navController.navigate("team/kick/$teamName") },
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
