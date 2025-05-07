package com.ballog.mobile.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ballog.mobile.navigation.Routes
import com.ballog.mobile.ui.theme.pretendard
import com.ballog.mobile.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.ui.theme.Surface

@Composable
fun SignupProfileScreen(navController: NavController) {
    var profileImage by remember { mutableStateOf<Int?>(null) } // 실제 구현 시 Uri 등으로 대체

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
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "4/4",
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
                text = "프로필 이미지를 선택해주세요",
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 23.87.sp,
                    color = Gray.Gray700
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "멋진 선수 이미지로 바꿔드릴게요!",
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 19.09.sp,
                    color = Gray.Gray700
                )
            )
            Spacer(modifier = Modifier.height(64.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .align(Alignment.CenterHorizontally)
                    .aspectRatio(0.7f)
                    .heightIn(max = 160.dp)
                    .shadow(12.dp, RoundedCornerShape(26.3.dp), clip = false)
                    .border(2.dp, Color(0xFF9BA0A5), RoundedCornerShape(26.3.dp))
                    .clip(RoundedCornerShape(26.3.dp))
                    .background(Surface)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // 상단 이미지 영역
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(topStart = 26.3.dp, topEnd = 26.3.dp))
                            .background(Gray.Gray600),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Image(
                                painter = painterResource(id = profileImage ?: R.drawable.defaultimage),
                                contentDescription = "프로필 이미지",
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                            )
                            // 업로드 버튼을 이미지 중앙에 겹치게
                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(36.dp)
                                    .align(Alignment.Center)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1B1B1D))
                                    .clickable {
                                        // TODO: 이미지 업로드 기능 구현
                                        profileImage = R.drawable.defaultimage // 임시: 업로드 시 이미지로 대체
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_camera),
                                        contentDescription = "이미지 업로드",
                                        modifier = Modifier.size(20.dp),
                                        colorFilter = ColorFilter.tint(Primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "이미지 업로드",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Primary,
                                        fontFamily = pretendard
                                    )
                                }
                            }
                        }
                    }
                    // 이름 영역
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(Gray.Gray500),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "BALLOG",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            fontFamily = pretendard
                        )
                    }
                    // 하단 능력치
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(bottomStart = 26.3.dp, bottomEnd = 26.3.dp))
                            .background(Gray.Gray600)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatBarCard(label = "Speed", value = 78)
                        StatBarCard(label = "Stamina", value = 80)
                        StatBarCard(label = "Attack", value = 64)
                        StatBarCard(label = "Defense", value = 80)
                        StatBarCard(label = "Recovery", value = 76)
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            BallogButton(
                onClick = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                type = ButtonType.BOTH,
                buttonColor = ButtonColor.GRAY,
                icon = painterResource(id = R.drawable.ic_trash),
                label = "건너뛰기",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            BallogButton(
                onClick = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.BLACK,
                label = "볼로그 시작하기",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )
        }
    }
}

@Composable
private fun StatBarCard(label: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().height(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Surface,
            fontFamily = pretendard,
            modifier = Modifier.width(70.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Gray.Gray500)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value / 100f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Primary)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value.toString(),
            fontSize = 16.8.sp,
            fontWeight = FontWeight.Bold,
            color = Surface,
            fontFamily = pretendard
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignupProfileScreenPreview() {
    SignupProfileScreen(navController = rememberNavController())
}
