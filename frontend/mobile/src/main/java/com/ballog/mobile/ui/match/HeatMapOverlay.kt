package com.ballog.mobile.ui.match

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.ballog.mobile.ui.theme.Primary

@Composable
fun HeatMapOverlay(
    heatData: List<List<Int>>,
    color: Color = Primary,
    modifier: Modifier = Modifier
) {
    if (heatData.isEmpty() || heatData[0].isEmpty()) return

    val gridX = heatData.size
    val gridY = heatData[0].size
    val maxCount = heatData.flatten().maxOrNull()?.takeIf { it > 0 } ?: 1

    Canvas(modifier = modifier) {
        val cellWidth = size.width / gridX
        val cellHeight = size.height / gridY

        for (i in 0 until gridX) {
            for (j in 0 until gridY) {
                val count = heatData[i][j]
                if (count > 0) {
                    val alpha = (count.toFloat() / maxCount).coerceIn(0.2f, 1f)
                    drawRect(
                        color = color.copy(alpha = alpha),
                        topLeft = Offset(i * cellWidth, j * cellHeight),
                        size = Size(cellWidth, cellHeight)
                    )
                }
            }
        }
    }
}
