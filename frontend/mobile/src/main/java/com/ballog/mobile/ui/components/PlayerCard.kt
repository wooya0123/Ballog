package com.ballog.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.R
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.ui.theme.pretendard
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.shadow

@Composable
fun PlayerCardFigma(
    name: String,
    imageRes: Int = R.drawable.ic_profile, // 임시 이미지
    stats: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(250.dp)
            .shadow(
                elevation = 32.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Primary.copy(alpha = 0.5f),
                spotColor = Primary.copy(alpha = 0.7f)
            )
            .background(Color.White, shape = RoundedCornerShape(22.dp))
            .border(1.dp, Gray.Gray400, shape = RoundedCornerShape(22.dp))
            .padding(0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 상단 이미지 영역 (전체 채우기)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                    .background(Gray.Gray700),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Player Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            // 이름
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Gray.Gray500)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    fontFamily = pretendard
                )
            }
            // 스탯 리스트
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Gray.Gray700)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                stats.forEach { (statName, statValue) ->
                    val valueInt = statValue.toIntOrNull() ?: 0
                    val ratio = (valueInt.coerceIn(0, 100)) / 100f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = statName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = pretendard,
                            modifier = Modifier.width(64.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ratio)
                                    .background(Primary, RoundedCornerShape(4.dp))
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statValue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = pretendard,
                            modifier = Modifier.width(32.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPlayerCardFigma() {
    PlayerCardFigma(
        name = "KIM GAHEE",
        stats = listOf(
            "Speed" to "78",
            "Stamina" to "80",
            "Attack" to "64",
            "Defense" to "80",
            "Recovery" to "76"
        )
    )
}
