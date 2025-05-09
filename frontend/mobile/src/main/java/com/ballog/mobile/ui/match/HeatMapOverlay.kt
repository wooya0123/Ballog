package com.ballog.mobile.ui.match

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun HeatMapOverlay(
    heatData: List<List<Int>>,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF20E9F5)
) {
    val cols = 15
    val rows = 10

    val density = LocalDensity.current

    Canvas(modifier = modifier.fillMaxSize()) {
        with(density) {
            val cellWidth = 16.dp.toPx()
            val cellHeight = 16.dp.toPx()
            val spacingX = 5.dp.toPx() // col 하나당 21dp → 셀16 + 간격5
            val spacingY = 5.dp.toPx()

            val alphaMap = listOf(0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f)

            val totalWidth = cellWidth * cols + spacingX * (cols - 1)
            val totalHeight = cellHeight * rows + spacingY * (rows - 1)

            val offsetX = (size.width - totalWidth) / 2
            val offsetY = (size.height - totalHeight) / 2

            for (x in 0 until cols) {
                for (y in 0 until rows) {
                    val intensity = heatData.getOrNull(x)?.getOrNull(y) ?: 0
                    val alpha = alphaMap.getOrElse(intensity.coerceIn(0, 5)) { 0f }

                    val left = offsetX + x * (cellWidth + spacingX)
                    val top = offsetY + y * (cellHeight + spacingY)

                    drawRoundRect(
                        color = color.copy(alpha = alpha),
                        topLeft = Offset(left, top),
                        size = Size(cellWidth, cellHeight)
                    )
                }
            }
        }
    }
}
