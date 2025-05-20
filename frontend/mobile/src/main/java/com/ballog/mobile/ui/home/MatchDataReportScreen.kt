package com.ballog.mobile.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.R
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.ui.theme.pretendard
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.viewmodel.UserViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@Composable
fun MatchDataReportScreen(
    nickname: String,
    onBack: () -> Unit = {},
    viewModel: UserViewModel = viewModel()
) {
    val aiRecommend = viewModel.aiRecommend.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.fetchAiRecommend()
    }

    val conclusion = aiRecommend?.conclusion
    val analysis = aiRecommend?.analysis
    val similarPlayerName = aiRecommend?.recommendedPlayer?.name
    val similarPlayerPosition = aiRecommend?.recommendedPlayer?.position
    val style = aiRecommend?.recommendedPlayer?.style
    val reason = aiRecommend?.recommendedPlayer?.reason
    val imageUrl = aiRecommend?.recommendedPlayer?.imageUrl

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopNavItem(
            title = "경기 분석 레포트",
            type = TopNavType.DETAIL_WITH_BACK,
            onBackClick = onBack,
        )
        // 스크롤 가능한 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // 플레이스타일 요약
            Text(
                text = "${nickname}님의 플레이스타일은...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Primary,
                fontFamily = pretendard,
                modifier = Modifier.padding(start = 24.dp, bottom = 2.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = conclusion.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Gray.Gray700,
                fontFamily = pretendard,
                modifier = Modifier.padding(start = 24.dp, bottom = 4.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = analysis.toString(),
                fontSize = 14.sp,
                color = Gray.Gray500,
                fontFamily = pretendard,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
            )
            // 비슷한 선수 카드
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${nickname}님과 비슷한 프로 축구 선수",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Gray.Gray700,
                fontFamily = pretendard,
                modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Color(0xFF1b1b1d), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = similarPlayerName.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Primary,
                        fontFamily = pretendard
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // 선수 이미지
                    Box(
//                        modifier = Modifier
//                            .size(118.dp)
//                            .background(Color.Gray, RoundedCornerShape(59.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "선수 이미지",
                                modifier = Modifier.size(120.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_profile),
                                contentDescription = "선수 이미지",
                                tint = Color.White,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = similarPlayerPosition.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        fontFamily = pretendard
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // 하이라이트 카드
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 157.dp)
                    .background(Color(0xFFF2F5F8), RoundedCornerShape(8.dp)),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "플레이 스타일",
                        fontSize = 14.sp,
                        color = Gray.Gray500,
                        fontFamily = pretendard
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = style.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        fontFamily = pretendard,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "유사점",
                        fontSize = 14.sp,
                        color = Gray.Gray500,
                        fontFamily = pretendard
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reason.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        fontFamily = pretendard
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


