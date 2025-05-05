package com.ballog.mobile.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.ui.theme.pretendard
import androidx.navigation.NavController
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.navigation.Routes
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.clickable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Surface
import com.ballog.mobile.ui.theme.System

@Composable
fun SignupVerificationScreen(
    navController: NavController,
    email: String
) {
    var verificationCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isFocused = remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1f at 0
                0f at 500
                1f at 1000
            },
            repeatMode = RepeatMode.Restart
        ), label = "cursorAnim"
    )

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
                text = "1/4",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray.Gray800,
                fontFamily = pretendard
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "이메일로 온 6자리 인증번호를\n입력해주세요.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 23.87.sp,
                color = Gray.Gray700,
                fontFamily = pretendard
            )

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    },
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = verificationCode,
                    onValueChange = {
                        if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                            verificationCode = it
                            errorMessage = null
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    textStyle = TextStyle(
                        color = Color.Transparent,
                        fontSize = 24.sp
                    ),
                    cursorBrush = SolidColor(Color.Transparent),
                    modifier = Modifier
                        .width(280.dp)
                        .height(48.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged { isFocused.value = it.isFocused },
                    decorationBox = { innerTextField ->
                        Box {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.width(280.dp)
                            ) {
                                for (i in 0 until 6) {
                                    val char = verificationCode.getOrNull(i)?.toString() ?: ""
                                    val showCursor = isFocused.value && verificationCode.length == i
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(width = 40.dp, height = 48.dp)
                                            .background(
                                                color = Gray.Gray300,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (errorMessage != null) System.Red else Color.Transparent,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                    ) {
                                        if (char.isNotEmpty()) {
                                            Text(
                                                text = char,
                                                fontFamily = pretendard,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp,
                                                lineHeight = 28.64.sp,
                                                color = Gray.Gray700,
                                                textAlign = TextAlign.Center
                                            )
                                        } else if (showCursor) {
                                            Box(
                                                Modifier
                                                    .width(2.dp)
                                                    .height(28.dp)
                                                    .background(Gray.Gray300.copy(alpha = cursorAlpha), RoundedCornerShape(1.dp))
                                            )
                                        }
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .alpha(0f)
                            ) {
                                innerTextField()
                            }
                        }
                    },
                    maxLines = 1
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 14.32.sp,
                        color = System.Red
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            BallogButton(
                onClick = {
                    if (verificationCode.length == 6) {
                        if (verificationCode == "123456") {
                            navController.navigate(Routes.SIGNUP_NICKNAME) {
                                popUpTo(Routes.SIGNUP_EMAIL_VERIFICATION) { inclusive = true }
                            }
                        } else {
                            errorMessage = "인증번호가 올바르지 않습니다."
                        }
                    } else {
                        errorMessage = "인증번호 6자리를 모두 입력해주세요."
                    }
                },
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.BLACK,
                label = "이메일 인증하기",
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )
        }
    }
}
