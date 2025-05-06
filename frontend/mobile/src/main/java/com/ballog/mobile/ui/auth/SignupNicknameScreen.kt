package com.ballog.mobile.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.Input
import com.ballog.mobile.ui.theme.pretendard
import com.ballog.mobile.navigation.Routes
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Surface
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun SignupNicknameScreen(
    navController: NavController
) {
    var nickname by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Request focus when the screen mounts
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(53.dp))

            Text(
                text = "2/4",
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 19.09.sp,
                    color = Gray.Gray800
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "닉네임을 정해주세요.",
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 23.87.sp,
                    color = Gray.Gray700
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            Input(
                value = nickname,
                onValueChange = {
                    nickname = it
                    hasError = false
                    errorMessage = null
                },
                placeholder = "닉네임",
                hasError = hasError,
                errorMessage = errorMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Spacer(modifier = Modifier.weight(1f))

            BallogButton(
                onClick = {
                    if (nickname.isBlank()) {
                        hasError = true
                        errorMessage = "닉네임을 입력해주세요."
                    } else {
                        navController.navigate(Routes.SIGNUP_BIRTHDAY) {
                            popUpTo(Routes.SIGNUP_NICKNAME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.BLACK,
                label = "다음",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )
        }
    }
}
