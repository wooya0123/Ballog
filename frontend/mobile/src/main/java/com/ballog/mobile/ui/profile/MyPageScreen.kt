package com.ballog.mobile.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ballog.mobile.navigation.Routes
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.System
import com.ballog.mobile.ui.theme.BallogTheme
import com.ballog.mobile.ui.theme.pretendard

@Composable
fun MyPageScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
            .padding(horizontal = 28.dp)
    ) {
        Text(
            text = "마이페이지",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = pretendard,
            color = Gray.Gray700,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        MyPageMenuItem("정보 수정") {
            navController.navigate(Routes.PROFILE_EDIT)
        }

        MyPageMenuItem("좋아요한 영상") {
            navController.navigate(Routes.MYPAGE_LIKED_VIDEOS)
        }

        MyPageMenuItem("로그아웃") {
            // TODO: 로그아웃 처리
        }

        MyPageMenuItem("회원 탈퇴", isWarning = true) {
            // TODO: 회원 탈퇴 처리
        }
    }
}

@Composable
private fun MyPageMenuItem(
    text: String,
    isWarning: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Gray.Gray100
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .padding(start = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Medium,
                    color = if (isWarning) System.Red else Gray.Gray800
                )
            }
            HorizontalDivider(
                color = Gray.Gray200,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyPageScreenPreview() {
    BallogTheme {
        MyPageScreen(navController = rememberNavController())
    }
}
