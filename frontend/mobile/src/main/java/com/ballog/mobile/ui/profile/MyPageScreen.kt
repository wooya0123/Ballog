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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ballog.mobile.navigation.Routes
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.BallogTheme
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import com.ballog.mobile.ui.theme.System
import com.ballog.mobile.ui.theme.pretendard

@Composable
fun MyPageScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        TopNavItem(
            title = "마이 페이지",
            type = TopNavType.MAIN_BASIC,
            navController = navController
        )
        Spacer(modifier = Modifier.height(16.dp))
        // 메뉴 리스트 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
        ) {
            MyPageMenuRow(
                text = "정보 수정",
                onClick = { navController.navigate("profile/edit") }
            )
            Divider(color = Gray.Gray200, thickness = 1.dp)
            MyPageMenuRow(
                text = "좋아요한 영상",
                onClick = { navController.navigate("mypage/liked-videos") }
            )
            Divider(color = Gray.Gray200, thickness = 1.dp)
            MyPageMenuRow(
                text = "로그아웃",
                onClick = { /* TODO: 로그아웃 처리 */ }
            )
            Divider(color = Gray.Gray200, thickness = 1.dp)
            MyPageMenuRow(
                text = "회원 탈퇴",
                isWarning = true,
                onClick = { /* TODO: 회원 탈퇴 처리 */ }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MyPageMenuRow(
    text: String,
    isWarning: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            color = if (isWarning) System.Red else Gray.Gray800,
            modifier = Modifier.padding(start = 0.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyPageScreenPreview() {
    BallogTheme {
        MyPageScreen(navController = rememberNavController())
    }
}
