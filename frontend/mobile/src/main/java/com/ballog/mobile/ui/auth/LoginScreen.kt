package com.ballog.mobile.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.Input
import com.ballog.mobile.ui.theme.pretendard
import kotlinx.coroutines.delay

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    return email.matches(emailRegex.toRegex())
}

sealed class LoginResult {
    object Success : LoginResult()
    object WrongPassword : LoginResult()
    object EmailNotFound : LoginResult()
}

// 임시 로그인 API 요청 함수
private suspend fun requestLogin(email: String, password: String): LoginResult {
    // TODO: 실제 API 요청으로 대체
    delay(1000) // API 요청 시뮬레이션
    return when {
        email == "test@ballog.com" && password == "password123" -> LoginResult.Success
        email == "test@ballog.com" -> LoginResult.WrongPassword
        else -> LoginResult.EmailNotFound
    }
}

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onSignUpNavigate: (String, String) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var hasEmailError by remember { mutableStateOf(false) }
    var hasPasswordError by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf("") }
    var passwordErrorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            when (requestLogin(email, password)) {
                LoginResult.Success -> onLoginClick()
                LoginResult.WrongPassword -> {
                    hasPasswordError = true
                    passwordErrorMessage = "비밀번호가 틀렸습니다"
                    isLoading = false
                }
                LoginResult.EmailNotFound -> {
                    onSignUpNavigate(email, password)
                }
            }
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                color = Color(0xFF1B1B1D),
                fontFamily = pretendard
            )

            Spacer(modifier = Modifier.height(40.dp))

            Input(
                value = email,
                onValueChange = {
                    email = it
                    hasEmailError = false
                },
                placeholder = "이메일",
                hasError = hasEmailError,
                errorMessage = emailErrorMessage,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Input(
                value = password,
                onValueChange = {
                    password = it
                    hasPasswordError = false
                },
                placeholder = "비밀번호",
                hasError = hasPasswordError,
                errorMessage = passwordErrorMessage,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityChange = { isPasswordVisible = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "비밀번호를 잊으셨나요?",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { onForgotPasswordClick() },
                fontFamily = pretendard
            )

            BallogButton(
                onClick = {
                    when {
                        email.isEmpty() -> {
                            hasEmailError = true
                            emailErrorMessage = "이메일을 입력해주세요"
                        }
                        !isValidEmail(email) -> {
                            hasEmailError = true
                            emailErrorMessage = "올바른 이메일 형식이 아닙니다"
                        }
                        password.isEmpty() -> {
                            hasPasswordError = true
                        }
                        else -> {
                            isLoading = true
                        }
                    }
                },
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.BLACK,
                label = if (isLoading) "로그인 중..." else "회원가입 / 로그인",
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun LoginScreenErrorPreview() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var hasEmailError by remember { mutableStateOf(true) }
    var hasPasswordError by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                color = Color(0xFF1B1B1D),
                fontFamily = pretendard
            )

            Spacer(modifier = Modifier.height(40.dp))

            Input(
                value = email,
                onValueChange = { email = it },
                placeholder = "이메일",
                hasError = hasEmailError,
                errorMessage = "이메일을 입력해주세요",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Input(
                value = password,
                onValueChange = { password = it },
                placeholder = "비밀번호",
                hasError = hasPasswordError,
                errorMessage = "비밀번호를 입력해주세요",
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityChange = { isPasswordVisible = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "비밀번호를 잊으셨나요?",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { },
                fontFamily = pretendard
            )

            BallogButton(
                onClick = { },
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.BLACK,
                label = "회원가입 / 로그인",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Filled State")
@Composable
fun LoginScreenFilledPreview() {
    var email by remember { mutableStateOf("example@email.com") }
    var password by remember { mutableStateOf("password123") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var hasEmailError by remember { mutableStateOf(false) }
    var hasPasswordError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                color = Color(0xFF1B1B1D),
                fontFamily = pretendard
            )

            Spacer(modifier = Modifier.height(40.dp))

            Input(
                value = email,
                onValueChange = { email = it },
                placeholder = "이메일",
                hasError = hasEmailError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Input(
                value = password,
                onValueChange = { password = it },
                placeholder = "비밀번호",
                hasError = hasPasswordError,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityChange = { isPasswordVisible = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "비밀번호를 잊으셨나요?",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { },
                fontFamily = pretendard
            )

            BallogButton(
                onClick = { },
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.BLACK,
                label = "회원가입 / 로그인",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Password Visible State")
@Composable
fun LoginScreenPasswordVisiblePreview() {
    var email by remember { mutableStateOf("example@email.com") }
    var password by remember { mutableStateOf("mypassword123") }
    var isPasswordVisible by remember { mutableStateOf(true) }
    var hasEmailError by remember { mutableStateOf(false) }
    var hasPasswordError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                color = Color(0xFF1B1B1D),
                fontFamily = pretendard
            )

            Spacer(modifier = Modifier.height(40.dp))

            Input(
                value = email,
                onValueChange = { email = it },
                placeholder = "이메일",
                hasError = hasEmailError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Input(
                value = password,
                onValueChange = { password = it },
                placeholder = "비밀번호",
                hasError = hasPasswordError,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityChange = { isPasswordVisible = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "비밀번호를 잊으셨나요?",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { },
                fontFamily = pretendard
            )

            BallogButton(
                onClick = { },
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.BLACK,
                label = "회원가입 / 로그인",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )
        }
    }
}
