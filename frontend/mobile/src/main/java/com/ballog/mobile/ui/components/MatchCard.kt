package com.ballog.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.BallogTheme
import com.ballog.mobile.ui.theme.pretendard

@Composable
fun MatchCard(
    modifier: Modifier = Modifier,
    timeLabel: String,
    time: String,
    place: String
) {
    Column(
        modifier = modifier
            .width(312.dp)
            .height(92.dp)
            .background(Gray.Gray200, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timeLabel,
                color = Gray.Gray500,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = pretendard
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = time,
                color = Gray.Gray800,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = pretendard
            )
        }


        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = place,
            color = Gray.Gray800,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = pretendard,
            maxLines = 1
        )
    }
}

@Preview(showBackground = false)
@Composable
fun MatchCardPreview() {
    BallogTheme {
        MatchCard(
            timeLabel = "경기 시작시간",
            time = "15:00",
            place = "잠실 올림픽 공식 풋살 경기장"
        )
    }
}
