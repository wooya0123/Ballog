package com.ballog.mobile.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.theme.pretendard
import com.ballog.mobile.R
import com.ballog.mobile.navigation.Routes
import com.ballog.mobile.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "홈",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1B1D),
                fontFamily = pretendard
            )
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "홈 아이콘",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "환영합니다!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1B1B1D),
                fontFamily = pretendard
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            BallogButton(
                onClick = {
                    coroutineScope.launch {
                        println("HomeScreen - Starting logout process")
                        viewModel.logout()
                        println("HomeScreen - Logout completed, navigating to onboarding")
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                },
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.ALERT,
                label = "로그아웃",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )
        }
    }
}
