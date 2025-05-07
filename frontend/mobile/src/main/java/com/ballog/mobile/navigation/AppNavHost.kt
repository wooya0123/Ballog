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
    NavHost(navController = navController, startDestination = Routes.MYPAGE) { // ✅ 진입 지점 임시 변경
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
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { },
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
            SignupScreen(initialEmail = email, initialPassword = password, navController = navController)
        }

        composable(
            route = Routes.SIGNUP_EMAIL_VERIFICATION,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            SignupVerificationScreen(navController = navController, email = email)
        }

        composable(Routes.SIGNUP_NICKNAME) {
            SignupNicknameScreen(navController = navController)
        }

        composable(Routes.SIGNUP_BIRTHDAY) {
            SignupBirthScreen(navController = navController)
        }

        composable(Routes.SIGNUP_PROFILE_IMAGE) {
            SignupProfileScreen(navController = navController)
        }

        composable(Routes.MAIN) {
            MainScreen(navController = navController)
        }

        composable(Routes.MYPAGE) {
            MyPageScreen(navController = navController)
        }

        composable(Routes.PROFILE_EDIT) {
            ProfileEditScreen(navController = navController)
        }

        composable(Routes.MYPAGE_LIKED_VIDEOS) {
            LikedVideosScreen()
        }

        composable(Routes.TEAM_LIST) {
            TeamListScreen(navController = navController)
        }

        composable(Routes.TEAM_CREATE) {
            TeamCreateScreen(
                onNavigateBack = { navController.popBackStack() },
                onClose = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.TEAM_DETAIL,
            arguments = listOf(navArgument("teamName") { type = NavType.StringType })
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamDetailScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = { navController.popBackStack() },
                onSettingClick = { navController.navigate("team/settings/$teamName") }
            )
        }

        composable(
            route = Routes.TEAM_SETTINGS,
            arguments = listOf(navArgument("teamName") { type = NavType.StringType })
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamSettingScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onDelegateClick = { navController.navigate("team/delegate/$teamName") },
                onKickMemberClick = { navController.navigate("team/kick/$teamName") },
                onInviteLinkClick = { /* TODO */ },
                onDeleteTeamClick = { /* TODO */ },
                onLeaveTeamClick = { /* TODO */ }
            )
        }

        composable(
            route = Routes.TEAM_DELEGATE,
            arguments = listOf(navArgument("teamName") { type = NavType.StringType })
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamDelegateScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.TEAM_KICK,
            arguments = listOf(navArgument("teamName") { type = NavType.StringType })
        ) { backStackEntry ->
            val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
            TeamKickScreen(
                navController = navController,
                teamName = teamName,
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }
    }
}
