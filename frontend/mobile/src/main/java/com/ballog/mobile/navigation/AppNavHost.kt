package com.ballog.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ballog.mobile.ui.auth.HomeScreen
import com.ballog.mobile.ui.auth.LoginScreen
import com.ballog.mobile.ui.auth.OnboardingScreen
import com.ballog.mobile.ui.auth.SignupNicknameScreen
import com.ballog.mobile.ui.auth.SignupProfileScreen
import com.ballog.mobile.ui.auth.SignupScreen
import com.ballog.mobile.ui.auth.SignupVerificationScreen
import com.ballog.mobile.ui.main.MainScreen
import com.ballog.mobile.ui.screens.signup.SignupBirthScreen


@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Routes.ONBOARDING) {
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
                    navController.navigate("${Routes.SIGNUP}/$email/$password")
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

        composable(Routes.SIGNUP_NICKNAME) {
            SignupNicknameScreen(navController = navController)
        }

        composable(Routes.SIGNUP_BIRTHDAY) {
            SignupBirthScreen(navController = navController)
        }

        // 프로필 이미지 등록 화면 추가
        composable("signup/profile-image") {
            SignupProfileScreen(navController = navController)
        }

        // 메인 화면 추가
        composable(Routes.MAIN) {
            MainScreen(navController = navController)
        }

    }
}
