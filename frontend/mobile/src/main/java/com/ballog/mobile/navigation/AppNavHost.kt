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
import com.ballog.mobile.ui.auth.SignupNicknameScreen
import com.ballog.mobile.ui.auth.SignupProfileScreen
import com.ballog.mobile.ui.auth.SignupScreen
import com.ballog.mobile.ui.auth.SignupVerificationScreen
import com.ballog.mobile.ui.main.MainScreen
import com.ballog.mobile.ui.auth.SignupBirthScreen
import com.ballog.mobile.ui.team.TeamCreateScreen
import com.ballog.mobile.ui.team.TeamDetailScreen
import com.ballog.mobile.ui.team.TeamSettingScreen
import com.ballog.mobile.ui.team.TeamDelegateScreen
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
            OnboardingScreen(
                onEmailLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.TEAM_LIST) {
            TeamListScreen(navController = navController)
        }
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
