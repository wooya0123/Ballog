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
    NavHost(navController = navController, startDestination = Routes.ONBOARDING) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onEmailLoginClick = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    // TODO: 실제 로그인 성공 시 메인 화면으로 이동
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true } // 로그인 화면을 백스택에서 제거
                    }
                },
                onForgotPasswordClick = {
                    // TODO: 비밀번호 찾기 화면으로 이동
                },
                onSignUpNavigate = { email, password ->
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
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""
            SignupScreen(
                initialEmail = email,
                initialPassword = password,
                navController = navController
            )
        }

        composable(
            route = Routes.SIGNUP_EMAIL_VERIFICATION,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            SignupVerificationScreen(
                navController = navController,
                email = email
            )
        }

        composable(
            route = Routes.SIGNUP_NICKNAME,
            // 키보드 상태 유지를 위한 옵션
            arguments = listOf()
        ) {
            SignupNicknameScreen(navController = navController)
        }

        composable(
            route = Routes.SIGNUP_BIRTHDAY,
            // 키보드 상태 유지를 위한 옵션
            arguments = listOf()
        ) {
            SignupBirthScreen(navController = navController)
        }

        // 프로필 이미지 등록 화면 추가
        composable(Routes.SIGNUP_PROFILE_IMAGE) {
            SignupProfileScreen(navController = navController)
        }

        // 메인 화면 추가
        composable(Routes.MAIN) {
            MainScreen(navController = navController)
        }

        // 마이페이지 관련 화면 추가
        composable(Routes.MYPAGE) {
            MyPageScreen(navController = navController)
        }

        composable(Routes.PROFILE_EDIT) {
            ProfileEditScreen(navController = navController)
        }

        composable(Routes.MYPAGE_LIKED_VIDEOS) {
            LikedVideosScreen()
        }

        // HOME(메인) 화면 추가
        composable(Routes.TEAM_LIST) {
            TeamListScreen(navController = navController)
        }

        // 팀 생성 화면 추가
        composable(Routes.TEAM_CREATE) {
            TeamCreateScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }

        // 팀 상세 화면 추가
        composable(
            route = Routes.TEAM_DETAIL,
            arguments = listOf(
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamDetailScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = {
                    navController.popBackStack()
                },
                onSettingClick = {
                    navController.navigate("team/settings/$teamName")
                }
            )
        }

        // 팀 설정 화면 추가
        composable(
            route = Routes.TEAM_SETTINGS,
            arguments = listOf(
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamSettingScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = {
                    navController.popBackStack()
                },
                onCloseClick = {
                    navController.popBackStack()
                },
                onDelegateClick = {
                    navController.navigate("team/delegate/$teamName")
                },
                onKickMemberClick = {
                    navController.navigate("team/kick/$teamName")
                },
                onInviteLinkClick = {
                    // TODO: Show invite link dialog
                },
                onDeleteTeamClick = {
                    // TODO: Show delete team confirmation dialog
                },
                onLeaveTeamClick = {
                    // TODO: Show leave team confirmation dialog
                }
            )
        }

        // 팀 권한 위임 화면 추가
        composable(
            route = Routes.TEAM_DELEGATE,
            arguments = listOf(
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamDelegateScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = {
                    navController.popBackStack()
                },
                onCloseClick = {
                    navController.popBackStack()
                },
                onSaveClick = {
                    // TODO: Implement save logic
                    navController.popBackStack()
                }
            )
        }

        // 팀 멤버 강제 퇴장 화면 추가
        composable(
            route = Routes.TEAM_KICK,
            arguments = listOf(
                navArgument("teamName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamKickScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = {
                    navController.popBackStack()
                },
                onCloseClick = {
                    navController.popBackStack()
                },
                onSaveClick = {
                    // TODO: Implement save logic
                    navController.popBackStack()
                }
            )
        }

    }
}
