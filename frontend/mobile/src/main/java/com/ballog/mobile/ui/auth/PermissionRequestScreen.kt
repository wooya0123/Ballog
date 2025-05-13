package com.ballog.mobile.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.R
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.pretendard

@Composable
fun PermissionRequestScreen(
    onConnectClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        // 중앙 컨텐츠: 아이콘만
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.samsung_health_72x72),
                contentDescription = "App Icon",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_link),
                contentDescription = "ic_link",
                modifier = Modifier.size(30.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                    Gray.Gray700.copy(alpha = 0.4f)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.samsung_health_72x72),
                contentDescription = "Samsung Health",
                modifier = Modifier.size(60.dp)
            )
        }
        // 하단 컨텐츠: 텍스트, 점, 버튼
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Samsung Health 연결",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                color = Primary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "볼로그는 삼성헬스와 함께합니다",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                color = Gray.Gray700,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7EE4EA))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD4D9DE))
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            BallogButton(
                onClick = onConnectClick,
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.GRAY,
                label = "권한 설정하기",
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            BallogButton(
                onClick = onConnectClick,
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.BLACK,
                label = "다음",
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionRequestScreenPreview() {
    PermissionRequestScreen()
}
