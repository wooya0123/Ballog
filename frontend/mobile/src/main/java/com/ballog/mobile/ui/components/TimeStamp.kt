package com.ballog.mobile.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.ui.theme.pretendard
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TimeStamp(
    startTime: String,
    endTime: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp)
    ) {
        Text(
            text = startTime,
            fontSize = 11.sp,
            color = Primary,
            fontWeight = FontWeight.Medium,
            fontFamily = pretendard
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "-",
            fontSize = 11.sp,
            color = Primary,
            fontWeight = FontWeight.Medium,
            fontFamily = pretendard
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = endTime,
            fontSize = 11.sp,
            color = Primary,
            fontWeight = FontWeight.Medium,
            fontFamily = pretendard
        )
    }
}
