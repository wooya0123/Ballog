package com.ballog.mobile.data.service

import android.content.Context
import android.util.Log
import com.ballog.mobile.data.model.GpsPoint
import com.ballog.mobile.data.repository.MatchRepository

class ExerciseMetricsCalculator(
    private val context: Context
) {
    fun calculateHeatmap(gpsPoints: List<GpsPoint>): List<List<Int>> {
        val grid = Array(16) { IntArray(10) { 0 } }
        val cellCounts = mutableMapOf<Pair<Int, Int>, Int>()

        if (gpsPoints.isEmpty()) return grid.map { it.toList() }

//        // Min-Max 정규화 사용 (주석 해제하여 사용)
//        Log.d("Heatmap", "Min-Max 정규화를 사용합니다.")
//        val minLat = gpsPoints.minOf { it.latitude }
//        val maxLat = gpsPoints.maxOf { it.latitude }
//        val minLng = gpsPoints.minOf { it.longitude }
//        val maxLng = gpsPoints.maxOf { it.longitude }
//
//        Log.d("Heatmap", "GPS 포인트 범위: 위도($minLat ~ $maxLat), 경도($minLng ~ $maxLng)")
//
//        gpsPoints.forEach { point ->
//            val normalizedLat = (point.latitude - minLat) / (maxLat - minLat)
//            val normalizedLng = (point.longitude - minLng) / (maxLng - minLng)
//
//            val row = (normalizedLat * 15).toInt().coerceIn(0, 15)
//            val col = (normalizedLng * 9).toInt().coerceIn(0, 9)
//
//            val key = Pair(row, col)
//            cellCounts[key] = (cellCounts[key] ?: 0) + 1
//        }

        // 경기장 좌표 기반 정규화 사용 (주석 해제하여 사용)
        Log.d("Heatmap", "경기장 좌표 기반 정규화를 사용합니다.")
        val matchRepository = MatchRepository(context)
        val fieldCorners = matchRepository.getFieldCorners()
        if (fieldCorners != null) {
            val minLat = fieldCorners.minOf { it.latitude }
            val maxLat = fieldCorners.maxOf { it.latitude }
            val minLng = fieldCorners.minOf { it.longitude }
            val maxLng = fieldCorners.maxOf { it.longitude }

            gpsPoints.forEach { point ->
                val normalizedLat = (point.latitude - minLat) / (maxLat - minLat)
                val normalizedLng = (point.longitude - minLng) / (maxLng - minLng)
                
                val row = (normalizedLat * 15).toInt().coerceIn(0, 15)
                val col = (normalizedLng * 9).toInt().coerceIn(0, 9)
                
                val key = Pair(row, col)
                cellCounts[key] = (cellCounts[key] ?: 0) + 1
            }
        }

        // 최대 카운트 찾기
        val maxCount = cellCounts.values.maxOrNull() ?: 1

        // 1-10 범위로 정규화하여 그리드에 저장
        cellCounts.forEach { (pos, count) ->
            val normalizedValue = if (count == 0) 0 else {
                1 + ((count * 9) / maxCount)  // 1-10 범위로 정규화
            }
            grid[pos.first][pos.second] = normalizedValue
        }

        Log.d("Heatmap", "정규화 - 최대 카운트: $maxCount")
        return grid.map { it.toList() }
    }
} 
