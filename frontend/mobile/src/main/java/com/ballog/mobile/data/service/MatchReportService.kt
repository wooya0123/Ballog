package com.ballog.mobile.data.service

import android.content.Context
import android.util.Log
import com.ballog.mobile.data.api.MatchApi
import com.ballog.mobile.data.dto.GpsLocation
import com.ballog.mobile.data.model.Exercise
import com.ballog.mobile.data.model.GpsPoint
import com.ballog.mobile.data.model.Match
import com.ballog.mobile.data.repository.MatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 경기 리포트 데이터를 생성하고 서버에 전송하는 서비스
 */
class MatchReportService(
    private val context: Context,
    private val matchApi: MatchApi,
    private val samsungHealthDataService: SamsungHealthDataService
) {
    private val TAG = "MatchReport"
    private val matchRepository = MatchRepository(context)

    /**
     * 삼성 헬스 데이터를 가져와 경기 리포트를 생성하고 서버에 전송합니다.
     *
     * @param matchId 경기 ID
     * @param matchDate 경기 날짜 (yyyy-MM-dd 형식)
     * @param quarterData 쿼터 정보 리스트 (쿼터 번호, 사이드, 시작/종료 시간 등)
     * @param token 토큰
     * @return 성공 여부
     */
    suspend fun createAndSendMatchReport(
        matchId: String,
        matchDate: String,
        quarterData: List<QuarterData>,
        token: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // 1. 삼성 헬스 데이터 가져오기
            val exerciseList = samsungHealthDataService.getExercise()
            if (exerciseList.isEmpty()) {
                Log.e(TAG, "삼성 헬스 데이터가 없습니다")
                return@withContext false
            }

            Log.d(TAG, "삼성 헬스 데이터 ${exerciseList.size}개 로드 성공")

            // 2. 경기장 모서리 좌표 가져오기
            val fieldCorners = matchRepository.getFieldCorners()
            if (fieldCorners == null) {
                Log.e(TAG, "경기장 좌표 데이터가 없습니다")
                return@withContext false
            }

            Log.d(TAG, "경기장 좌표 데이터 로드 성공")

            // 3. 쿼터별 리포트 데이터 생성
            val reportDataList = createQuarterReports(exerciseList, quarterData, fieldCorners)
            if (reportDataList.isEmpty()) {
                Log.e(TAG, "생성된 리포트 데이터가 없습니다")
                return@withContext false
            }

            // 4. JSON 객체 생성
            val requestBody = createRequestBody(matchDate, reportDataList)

            // 5. 서버에 전송
            val response = matchApi.sendMatchReport(matchId, requestBody, token)

            Log.d(TAG, "리포트 전송 결과: ${response.isSuccessful}")
            return@withContext response.isSuccessful

        } catch (e: Exception) {
            Log.e(TAG, "리포트 생성 및 전송 실패: ${e.message}")
            return@withContext false
        }
    }

    /**
     * 쿼터별 리포트 데이터를 생성합니다.
     */
    private fun createQuarterReports(
        exerciseList: List<Exercise>,
        quarterData: List<QuarterData>,
        fieldCorners: List<GpsLocation>
    ): List<JSONObject> {
        val reportDataList = mutableListOf<JSONObject>()

        for (quarter in quarterData) {
            // 1. 시간 범위에 맞는 운동 데이터 필터링
            val exercise = findExerciseForTimeRange(
                exerciseList,
                quarter.startTime,
                quarter.endTime
            ) ?: continue

            // 2. 경기장 내 GPS 포인트만 필터링
            val filteredGpsPoints = filterGpsPointsInsideField(exercise.gpsPoints, fieldCorners)

            // 3. GPS 포인트를 그리드로 변환
            val heatmapGrid = createHeatmapGrid(filteredGpsPoints, fieldCorners)

            // 4. 쿼터 리포트 JSON 생성
            val reportData = createQuarterReportJson(quarter, exercise, heatmapGrid)
            reportDataList.add(reportData)

            Log.d(TAG, "쿼터 ${quarter.quarterNumber} 리포트 생성 완료")
        }

        return reportDataList
    }

    /**
     * 시간 범위에 맞는 운동 데이터를 찾습니다.
     */
    private fun findExerciseForTimeRange(
        exerciseList: List<Exercise>,
        startTime: String,
        endTime: String
    ): Exercise? {
        // 시간 파싱 (HH:mm 형식)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startTimeMillis = timeFormat.parse(startTime)?.time ?: return null
        val endTimeMillis = timeFormat.parse(endTime)?.time ?: return null

        // 가장 가까운 운동 데이터 찾기 (현재는 단순히 첫 번째 데이터 반환)
        // 실제로는 시간 범위를 비교하는 로직 필요
        return exerciseList.firstOrNull()
    }

    /**
     * 경기장 내부에 있는 GPS 포인트만 필터링합니다.
     */
    private fun filterGpsPointsInsideField(
        gpsPoints: List<GpsPoint>,
        fieldCorners: List<GpsLocation>
    ): List<GpsPoint> {
        return gpsPoints.filter { point ->
            val location = GpsLocation(point.latitude, point.longitude)
            isPointInsideField(location, fieldCorners)
        }
    }

    /**
     * 점이 경기장 내부에 있는지 확인합니다.
     */
    private fun isPointInsideField(
        point: GpsLocation,
        fieldCorners: List<GpsLocation>
    ): Boolean {
        // Ray Casting 알고리즘 사용
        var inside = false
        var j = fieldCorners.size - 1

        for (i in fieldCorners.indices) {
            val xi = fieldCorners[i].longitude
            val yi = fieldCorners[i].latitude
            val xj = fieldCorners[j].longitude
            val yj = fieldCorners[j].latitude

            val intersect = ((yi > point.latitude) != (yj > point.latitude)) &&
                (point.longitude < (xj - xi) * (point.latitude - yi) / (yj - yi) + xi)

            if (intersect) inside = !inside
            j = i
        }

        return inside
    }

    /**
     * GPS 포인트를 16x10 그리드로 변환합니다.
     */
    private fun createHeatmapGrid(
        gpsPoints: List<GpsPoint>,
        fieldCorners: List<GpsLocation>
    ): Array<IntArray> {
        // 1. 그리드 초기화
        val grid = Array(10) { IntArray(16) }

        // 2. 각 GPS 포인트를 그리드 위치로 변환하여 카운트 증가
        for (point in gpsPoints) {
            val location = GpsLocation(point.latitude, point.longitude)
            val gridPosition = convertToGridPosition(location, fieldCorners)

            if (gridPosition != null) {
                val (row, col) = gridPosition
                if (row in 0..9 && col in 0..15) {
                    grid[row][col]++
                }
            }
        }

        // 3. 그리드 값을 1-10 범위로 정규화
        return normalizeGrid(grid)
    }

    /**
     * 좌표를 그리드 위치로 변환합니다.
     */
    private fun convertToGridPosition(
        location: GpsLocation,
        fieldCorners: List<GpsLocation>
    ): Pair<Int, Int>? {
        // 1. 경기장 내부인지 확인
        if (!isPointInsideField(location, fieldCorners)) {
            return null
        }

        // 2. 경기장 범위 계산
        val minLat = fieldCorners.minOf { it.latitude }
        val maxLat = fieldCorners.maxOf { it.latitude }
        val minLng = fieldCorners.minOf { it.longitude }
        val maxLng = fieldCorners.maxOf { it.longitude }

        // 3. 위치를 0-1 범위로 정규화
        val normalizedX = (location.longitude - minLng) / (maxLng - minLng)
        val normalizedY = (location.latitude - minLat) / (maxLat - minLat)

        // 4. 그리드 셀 계산
        val gridCol = (normalizedX * 16).toInt().coerceIn(0, 15)
        val gridRow = (normalizedY * 10).toInt().coerceIn(0, 9)

        return Pair(gridRow, gridCol)
    }

    /**
     * 그리드 값을 1-10 범위로 정규화합니다.
     */
    private fun normalizeGrid(grid: Array<IntArray>): Array<IntArray> {
        val result = Array(10) { IntArray(16) }

        // 최대값 찾기
        var maxValue = 0
        for (row in 0..9) {
            for (col in 0..15) {
                maxValue = maxOf(maxValue, grid[row][col])
            }
        }

        // 값이 없으면 반환
        if (maxValue == 0) {
            return result
        }

        // 1-10 범위로 정규화
        for (row in 0..9) {
            for (col in 0..15) {
                val normalizedValue = if (grid[row][col] == 0) {
                    0
                } else {
                    1 + ((grid[row][col] * 9) / maxValue)
                }
                result[row][col] = normalizedValue
            }
        }

        return result
    }

    /**
     * 쿼터 리포트 JSON 객체를 생성합니다.
     */
    private fun createQuarterReportJson(
        quarter: QuarterData,
        exercise: Exercise,
        heatmapGrid: Array<IntArray>
    ): JSONObject {
        val reportJson = JSONObject()
        val gameReportData = JSONObject()

        // 기본 정보 설정
        reportJson.put("quarterNumber", quarter.quarterNumber)
        reportJson.put("gameSide", quarter.gameSide)

        // 경기 데이터 설정
        gameReportData.put("startTime", quarter.startTime)
        gameReportData.put("endTime", quarter.endTime)
        gameReportData.put("distance", exercise.distance)
        gameReportData.put("avgSpeed", exercise.avgSpeed)
        gameReportData.put("maxSpeed", exercise.maxSpeed)
        gameReportData.put("calories", exercise.calories)
        gameReportData.put("sprint", exercise.sprintCount)
        gameReportData.put("avgHeartRate", exercise.avgHeartRate)
        gameReportData.put("maxHeartRate", exercise.maxHeartRate)

        // 히트맵 데이터 설정
        val heatmapArray = JSONArray()
        for (row in heatmapGrid) {
            val rowArray = JSONArray()
            for (value in row) {
                rowArray.put(value)
            }
            heatmapArray.put(rowArray)
        }
        gameReportData.put("heatmap", heatmapArray)

        // gameReportData를 reportJson에 추가
        reportJson.put("gameReportData", gameReportData)

        return reportJson
    }

    /**
     * 서버에 전송할 요청 본문을 생성합니다.
     */
    private fun createRequestBody(
        matchDate: String,
        reportDataList: List<JSONObject>
    ): JSONObject {
        val requestBody = JSONObject()

        // 경기 날짜 설정
        requestBody.put("matchDate", matchDate)

        // 리포트 데이터 배열 설정
        val reportArray = JSONArray()
        for (report in reportDataList) {
            reportArray.put(report)
        }
        requestBody.put("reportDataList", reportArray)

        return requestBody
    }

    /**
     * 쿼터 데이터 클래스
     */
    data class QuarterData(
        val quarterNumber: Int,
        val gameSide: String,
        val startTime: String,
        val endTime: String
    )
}
