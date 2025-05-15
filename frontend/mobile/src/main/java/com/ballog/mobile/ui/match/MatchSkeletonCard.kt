package com.ballog.mobile.ui.match

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ballog.mobile.ui.theme.Gray

@Composable
fun MatchSkeletonCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(Gray.Gray200, RoundedCornerShape(16.dp))
        )
        repeat(2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .background(Gray.Gray200, RoundedCornerShape(16.dp))
            )
        }
    }
}
