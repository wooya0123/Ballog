package com.ballog.mobile.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.ballog.mobile.ui.theme.pretendard
import androidx.compose.ui.tooling.preview.Preview
import com.ballog.mobile.ui.theme.Gray
import androidx.compose.ui.res.painterResource
import com.ballog.mobile.R
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.ButtonColor

@Composable
fun MatchDataCard(
    date: String,
    startTime: String,
    endTime: String,
    buttonText: String,
    modifier: Modifier = Modifier,
    onButtonClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .background(
                color = Gray.Gray200,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = Gray.Gray300,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Heading
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray.Gray700,
                    fontFamily = pretendard,
                    modifier = Modifier.weight(1f)
                )
                BallogButton(
                    onClick = { /* TODO: 삭제 콜백 구현 필요시 전달 */ },
                    type = ButtonType.ICON_ONLY,
                    buttonColor = ButtonColor.ALERT,
                    icon = painterResource(id = R.drawable.ic_trash),
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Detail
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "기록 시작시간",
                        fontSize = 12.sp,
                        color = Gray.Gray500,
                        fontFamily = pretendard
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = startTime,
                        fontSize = 15.sp,
                        color = Gray.Gray700,
                        fontFamily = pretendard
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "기록 종료시간",
                        fontSize = 12.sp,
                        color = Gray.Gray500,
                        fontFamily = pretendard
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = endTime,
                        fontSize = 15.sp,
                        color = Gray.Gray700,
                        fontFamily = pretendard
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Button
            BallogButton(
                onClick = { onButtonClick?.invoke() },
                type = ButtonType.LABEL_ONLY,
                buttonColor = if (buttonText == "정보 입력하기") ButtonColor.PRIMARY else ButtonColor.GRAY,
                label = buttonText,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun PreviewMatchDataCard() {
    MatchDataCard(
        date = "2025.05.08",
        startTime = "15:37",
        endTime = "15:50",
        buttonText = "정보 수정하기"
    )
} 
