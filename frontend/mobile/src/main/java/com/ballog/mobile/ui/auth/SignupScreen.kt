package com.ballog.mobile.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.Input
import com.ballog.mobile.ui.theme.pretendard
import androidx.navigation.NavController
import com.ballog.mobile.navigation.Routes
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Surface

@Composable
fun SignupScreen(
    initialEmail: String = "",
    initialPassword: String = "",
    navController: NavController
) {
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf(initialPassword) }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var hasEmailError by remember { mutableStateOf(false) }
    var hasPasswordError by remember { mutableStateOf(false) }
    var hasConfirmPasswordError by remember { mutableStateOf(false) }
    var confirmPasswordErrorMessage by remember { mutableStateOf("") }

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
                text = "풋살은 볼로그와 함께",
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                lineHeight = 28.64.sp,
                color = Gray.Gray700,
                fontFamily = pretendard
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "계정을 만들어 볼로그와 함께하세요!",
                fontSize = 15.sp,
                fontWeight = FontWeight.W200,
                lineHeight = 28.64.sp,
                color = Gray.Gray700,
                fontFamily = pretendard
            )

            Spacer(modifier = Modifier.height(20.dp))

            Input(
                value = email,
                onValueChange = { },  // 이메일 수정 불가능하도록 빈 람다
                placeholder = "이메일",
                hasError = hasEmailError,
                errorMessage = "이메일을 입력해주세요",
                modifier = Modifier.fillMaxWidth(),
                enabled = false  // 이메일 입력 비활성화
            )

            Spacer(modifier = Modifier.height(12.dp))

            Input(
                value = password,
                onValueChange = {
                    password = it
                    hasPasswordError = false
                    if (confirmPassword.isNotEmpty() && it != confirmPassword) {
                        hasConfirmPasswordError = true
                        confirmPasswordErrorMessage = "비밀번호가 일치하지 않습니다."
                    } else {
                        hasConfirmPasswordError = false
                    }
                },
                placeholder = "비밀번호",
                hasError = hasPasswordError,
                errorMessage = "비밀번호를 입력해주세요",
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityChange = { isPasswordVisible = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Input(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    if (it != password) {
                        hasConfirmPasswordError = true
                        confirmPasswordErrorMessage = "비밀번호가 일치하지 않습니다."
                    } else {
                        hasConfirmPasswordError = false
                    }
                },
                placeholder = "비밀번호 확인",
                hasError = hasConfirmPasswordError,
                errorMessage = confirmPasswordErrorMessage,
                isPassword = true,
                isPasswordVisible = isConfirmPasswordVisible,
                onPasswordVisibilityChange = { isConfirmPasswordVisible = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            BallogButton(
                onClick = {
                    hasEmailError = email.isEmpty()
                    hasPasswordError = password.isEmpty()
                    hasConfirmPasswordError = confirmPassword.isEmpty()

                    if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                        hasConfirmPasswordError = true
                        confirmPasswordErrorMessage = "비밀번호가 일치하지 않습니다."
                    }

                    if (!hasEmailError && !hasPasswordError && !hasConfirmPasswordError) {
                        navController.navigate(Routes.SIGNUP_EMAIL_VERIFICATION.replace("{email}", email))
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
