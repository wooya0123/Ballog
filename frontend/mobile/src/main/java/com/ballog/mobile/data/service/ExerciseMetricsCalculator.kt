package com.ballog.mobile.data.service

import android.content.Context
import android.util.Log
import com.ballog.mobile.data.model.GpsPoint
import com.ballog.mobile.data.repository.MatchRepository

class ExerciseMetricsCalculator(
    private val context: Context
) {
    // 로그 태그 상수 정의
    companion object {
        private const val TAG = "HeatmapCalc"
        private const val FIELD_INCLUSION_THRESHOLD = 0.8 // 80% 포함 기준
    }

    fun calculateHeatmap(gpsPoints: List<GpsPoint>): List<List<Int>> {
        val ROWS = 10
        val COLS = 16

        val grid = Array(ROWS) { IntArray(COLS) { 0 } }
        val cellCounts = mutableMapOf<Pair<Int, Int>, Int>()

        if (gpsPoints.isEmpty()) {
            Log.i(TAG, "GPS 포인트가 없습니다. 빈 히트맵 반환")
            return grid.map { it.toList() }
        }

        // 경기장 좌표 가져오기
        val matchRepository = MatchRepository(context)
        val fieldCorners = matchRepository.getFieldCorners()

        // 정규화 방식 결정
        val useFieldBasedNormalization = if (fieldCorners != null) {
            // 경기장 경계 계산
            val minLat = fieldCorners.minOf { it.latitude }
            val maxLat = fieldCorners.maxOf { it.latitude }
            val minLng = fieldCorners.minOf { it.longitude }
            val maxLng = fieldCorners.maxOf { it.longitude }

            Log.d(TAG, "경기장 좌표: 남서(${minLat}, ${minLng}), 북동(${maxLat}, ${maxLng})")

            // 경기장 내부에 포함된 GPS 포인트 개수 계산
            val pointsInField = gpsPoints.count { point ->
                point.latitude in minLat..maxLat && point.longitude in minLng..maxLng
            }

            val inclusionRatio = pointsInField.toDouble() / gpsPoints.size
            val inclusionPercent = (inclusionRatio * 100).toInt()

            Log.i(TAG, "경기장 내부 포함 GPS 포인트: $pointsInField/${gpsPoints.size} (${inclusionPercent}%)")

            // 80% 이상이 경기장 내부에 있으면 경기장 좌표 기반 정규화 사용
            inclusionRatio >= FIELD_INCLUSION_THRESHOLD
        } else {
            Log.w(TAG, "경기장 좌표가 없습니다. Min-Max 정규화 사용")
            false
        }

        // 선택된 정규화 방식 적용
        if (useFieldBasedNormalization && fieldCorners != null) {
            // 1. 경기장 좌표 기반 정규화 사용
            Log.i(TAG, "정규화 방식: 경기장 좌표 기반 (80% 이상 포함)")

            val minLat = fieldCorners.minOf { it.latitude }
            val maxLat = fieldCorners.maxOf { it.latitude }
            val minLng = fieldCorners.minOf { it.longitude }
            val maxLng = fieldCorners.maxOf { it.longitude }

            gpsPoints.forEach { point ->
                val normalizedLat = (point.latitude - minLat) / (maxLat - minLat)
                val normalizedLng = (point.longitude - minLng) / (maxLng - minLng)

                val row = (normalizedLat * (ROWS-1)).toInt().coerceIn(0, ROWS-1)
                val col = (normalizedLng * (COLS-1)).toInt().coerceIn(0, COLS-1)

                val key = Pair(row, col)
                cellCounts[key] = (cellCounts[key] ?: 0) + 1
            }
        } else {
            // 2. Min-Max 정규화 사용
            Log.i(TAG, "정규화 방식: Min-Max (80% 미만 포함 또는 경기장 좌표 없음)")

            val minLat = gpsPoints.minOf { it.latitude }
            val maxLat = gpsPoints.maxOf { it.latitude }
            val minLng = gpsPoints.minOf { it.longitude }
            val maxLng = gpsPoints.maxOf { it.longitude }

            Log.d(TAG, "GPS 포인트 범위: 남서(${minLat}, ${minLng}), 북동(${maxLat}, ${maxLng})")

            gpsPoints.forEach { point ->
                val normalizedLat = (point.latitude - minLat) / (maxLat - minLat)
                val normalizedLng = (point.longitude - minLng) / (maxLng - minLng)

                val row = (normalizedLat * (ROWS-1)).toInt().coerceIn(0, ROWS-1)
                val col = (normalizedLng * (COLS-1)).toInt().coerceIn(0, COLS-1)

                val key = Pair(row, col)
                cellCounts[key] = (cellCounts[key] ?: 0) + 1
            }
        }

        // 최대 카운트 찾기
        val maxCount = cellCounts.values.maxOrNull() ?: 1
        Log.d(TAG, "셀별 최대 카운트: $maxCount, 총 채워진 셀 수: ${cellCounts.size}")

        // 1-10 범위로 정규화하여 그리드에 저장
        cellCounts.forEach { (pos, count) ->
            // 안전 검사 추가: 배열 범위를 벗어나지 않도록
            if (pos.first in 0 until ROWS && pos.second in 0 until COLS) {
                val normalizedValue = if (count == 0) 0 else {
                    1 + ((count * 9) / maxCount)  // 1-10 범위로 정규화
                }
                grid[pos.first][pos.second] = normalizedValue
            }
        }

        // 최종 히트맵 정보 로깅
        val filledCells = grid.sumOf { row -> row.count { it > 0 } }
        val totalCells = ROWS * COLS
        val filledPercent = (filledCells * 100 / totalCells)

        Log.i(TAG, "히트맵 생성 완료: ${ROWS}x${COLS} 그리드, 채워진 셀: $filledCells/$totalCells (${filledPercent}%)")

        return grid.map { it.toList() }
    }
}
