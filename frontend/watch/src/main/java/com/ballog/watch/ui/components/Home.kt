package com.ballog.watch.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.ballog.watch.R

@Composable
fun HomeScreen(onMeasureClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 축구공 이미지
        Image(
            painter = painterResource(id = R.drawable.soccer_ball),
            contentDescription = "축구공",
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 측정하기 버튼 - 길다란 타원형, 시안 배경, 검은색 글씨
        BallogButton(
            text = "경기장 측정하기",
            onClick = onMeasureClick,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
