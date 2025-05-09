package com.ballog.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.pretendard

/**
 * bars: 최근 경기 데이터. 값이 true/false면 최근 경기만 색상, 나머지는 회색. 값이 Float이면 0~1로 길이 조절.
 * bars.size < 5면 나머지는 빈 바(짧은 회색)로 채움.
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    bars: List<Boolean>, // true: 최근 경기(색상), false: 이전(회색)
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val totalBars = 5
    val barWidth = 12.dp
    val barMaxHeight = 48.dp
    val barMinHeight = 12.dp
    val barSpacing = 6.dp
    val barsFull = List(totalBars - bars.size) { null } + bars.map { it }

    Column(
        modifier = modifier
            .background(Gray.Gray200, shape = RoundedCornerShape(20.dp))
            .width(150.dp)
            .height(160.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Gray.Gray500,
            fontFamily = pretendard,
            modifier = Modifier.padding(top = 12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Gray.Gray700,
            fontFamily = pretendard,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .height(barMaxHeight)
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            barsFull.forEachIndexed { idx, isRecent ->
                Box(
                    modifier = Modifier
                        .width(barWidth)
                        .height(
                            when (isRecent) {
                                true -> if (idx == barsFull.lastIndex) barMaxHeight else barMaxHeight * 0.7f
                                false -> barMaxHeight * 0.7f
                                null -> barMinHeight
                            }
                        )
                        .background(
                            when (isRecent) {
                                true -> if (idx == barsFull.lastIndex) barColor else Gray.Gray400
                                false, null -> Gray.Gray400
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                if (idx != barsFull.lastIndex) Spacer(modifier = Modifier.width(barSpacing))
            }
        }
    }
}
