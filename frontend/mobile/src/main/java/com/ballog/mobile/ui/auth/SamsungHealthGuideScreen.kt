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
import com.ballog.mobile.data.service.SamsungHealthDataService
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.pretendard
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.ballog.mobile.data.util.OnboardingPrefs

@Composable
fun SamsungHealthGuideScreen(
    onConnectClick: () -> Unit = {},
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        // 상단 컨텐츠: guide1, guide2 이미지를 세로로
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.guide1),
                contentDescription = "Guide 1",
                modifier = Modifier.height(120.dp)
                    .width(220.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.guide2),
                contentDescription = "Guide 2",
                modifier = Modifier.height(150.dp)
                    .width(250.dp)
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
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "[ 더보기 > + > 새 운동 만들기 ]",
                fontSize = 14.sp,
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
                        .background(Gray.Gray300)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Primary)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            BallogButton(
                onClick = {/* 설정하러 가기 동작(필요시 구현) */},
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.GRAY,
                label = "설정하러 가기",
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            BallogButton(
                onClick = {
                    OnboardingPrefs.setGuideCompleted(context, true)
                    onConnectClick()
                },
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
fun PreviewSamsungHealthGuideScreen() {
    SamsungHealthGuideScreen()
}
