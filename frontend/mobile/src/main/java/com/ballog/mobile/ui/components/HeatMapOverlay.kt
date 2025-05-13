package com.ballog.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ballog.mobile.R
import com.ballog.mobile.ui.match.HeatMap
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary


@Composable
fun HeatMapWithSideSelection(
    heatData: List<List<Int>>,
    selectedSide: String?,
    onSideSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(168.dp)
    ) {
        HeatMap(
            heatData = heatData,
            modifier = Modifier.matchParentSize()
        )
        Row(Modifier.matchParentSize()) {
            // 왼쪽 영역
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSideSelect("LEFT") }
            ) {
                if (selectedSide == "LEFT") {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Gray.Gray700.copy(alpha = 0.85f))
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_team),
                        contentDescription = "왼쪽 선택됨",
                        tint = Primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            // 오른쪽 영역
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSideSelect("RIGHT") }
            ) {
                if (selectedSide == "RIGHT") {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Gray.Gray700.copy(alpha = 0.98f))
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_team),
                        contentDescription = "오른쪽 선택됨",
                        tint = Primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
} 
