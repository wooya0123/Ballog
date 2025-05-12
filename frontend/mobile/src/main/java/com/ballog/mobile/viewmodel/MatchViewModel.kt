package com.ballog.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ballog.mobile.BallogApplication
import com.ballog.mobile.data.api.RetrofitInstance
import com.ballog.mobile.data.dto.GpsLocation
import com.ballog.mobile.data.dto.MatchItemDto
import com.ballog.mobile.data.dto.MatchRegisterRequest
import com.ballog.mobile.data.dto.TeamMatchRegisterRequest
import com.ballog.mobile.data.dto.TeamMember
import com.ballog.mobile.data.model.Match
import com.ballog.mobile.data.model.MatchState
import com.ballog.mobile.data.repository.MatchRepository
import com.ballog.mobile.data.service.MatchReportService
import com.ballog.mobile.data.service.SamsungHealthDataService
import com.ballog.mobile.ui.components.DateMarkerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.Date
import java.util.Locale

class MatchViewModel : ViewModel() {
    private val tokenManager = BallogApplication.getInstance().tokenManager
    private val matchApi = RetrofitInstance.matchApi
    private val context = BallogApplication.getInstance().applicationContext
    private val matchRepository = MatchRepository(context)
    private val samsungHealthService = SamsungHealthDataService(context)

    // 매치 상태 (로딩 / 성공 / 에러)
    private val _matchState = MutableStateFlow<MatchState>(MatchState.Loading)
    val matchState: StateFlow<MatchState> = _matchState

    // 리포트 전송 상태
    private val _reportState = MutableStateFlow<ReportState>(ReportState.Initial)
    val reportState: StateFlow<ReportState> = _reportState

    // 리포트 상태 봉인 클래스
    sealed class ReportState {
        object Initial : ReportState()
        object Loading : ReportState()
        object Success : ReportState()
        data class Error(val message: String?) : ReportState()
    }

    /**
     * 내 매치 리스트 불러오기
     * @param month yyyy-MM 형태의 월 문자열
     */
    fun fetchMyMatches(month: String) {
        viewModelScope.launch {
            _matchState.value = MatchState.Loading
            try {
                val token = tokenManager.getAccessToken().firstOrNull() ?: return@launch
                val response = matchApi.getMyMatches("Bearer $token", month)
                val body = response.body()

                android.util.Log.d("MatchViewModel", "✅ 응답 결과: ${body}")

                if (response.isSuccessful && body?.isSuccess == true) {
                    // Dto → Domain 변환 후 상태 갱신
                    val matches = body.result?.matchList?.map { it.toDomain() } ?: emptyList()
                    _matchState.value = MatchState.Success(matches)
                } else {
                    _matchState.value = MatchState.Error(body?.message ?: "매치 데이터를 불러오지 못했습니다")
                }
            } catch (e: Exception) {
                _matchState.value = MatchState.Error("네트워크 오류: ${e.localizedMessage}")
            }
        }
    }

    /**
     * 팀 매치 리스트 불러오기
     */
    fun fetchTeamMatches(teamId: Int, month: String) {
        viewModelScope.launch {
            _matchState.value = MatchState.Loading
            try {
                val token = tokenManager.getAccessToken().firstOrNull() ?: return@launch
                val response = matchApi.getTeamMatches("Bearer $token", teamId, month)
                val body = response.body()

                if (response.isSuccessful && body?.isSuccess == true) {
                    val matches = body.result?.matchList?.map { it.toDomain() } ?: emptyList()
                    _matchState.value = MatchState.Success(matches)
                } else {
                    _matchState.value = MatchState.Error(body?.message ?: "팀 매치를 불러오지 못했습니다")
                }
            } catch (e: Exception) {
                _matchState.value = MatchState.Error("네트워크 오류: ${e.localizedMessage}")
            }
        }
    }

    /**
     * 팀 맴버 불러오기
     */
    // 상태 선언
    private val _teamPlayers = MutableStateFlow<List<TeamMember>>(emptyList())
    val teamPlayers: StateFlow<List<TeamMember>> = _teamPlayers

    fun fetchTeamPlayers(teamId: Int) {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken().firstOrNull()
            if (token == null) {
                _teamPlayers.value = emptyList()
                return@launch
            }

            try {
                val response = RetrofitInstance.teamApi.getTeamMemberList("Bearer $token", teamId)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _teamPlayers.value = response.body()?.result?.teamMemberList ?: emptyList()

                    val members = response.body()?.result?.teamMemberList ?: emptyList()
                    _teamPlayers.value = members

                    // 로그 출력
                    android.util.Log.d(
                        "MatchViewModel",
                        "✅ 팀 멤버 로딩 성공: 총 ${members.size}명 → ${members.joinToString { it.nickname }}"
                    )
                } else {
                    _teamPlayers.value = emptyList()
                }
            } catch (e: Exception) {
                _teamPlayers.value = emptyList()
            }
        }
    }

    /**
     * 서버에 개인 신규 매치 등록하는 함수
     */
    fun registerMyMatch(
        date: String,
        startTime: String,
        endTime: String,
        matchName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken().firstOrNull()
            if (token == null) {
                onError("로그인이 필요합니다")
                return@launch
            }

            val request = MatchRegisterRequest(
                matchDate = date,
                startTime = startTime,
                endTime = endTime,
                matchName = matchName
            )

            val response = matchApi.registerMyMatch("Bearer $token", request)
            android.util.Log.d("MatchViewModel", "📤 요청 내용: $request")


            if (response.isSuccessful && response.body()?.isSuccess == true) {
                onSuccess()
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("MatchViewModel", "❌ 매치 등록 실패: code=${response.code()}, body=$errorBody")
                onError(response.body()?.message ?: "매치 등록 실패 (${response.code()})")
            }
        }
    }

    /**
     * 서버에 팀 신규 매치 등록 함수
     */
    fun registerTeamMatch(
        teamId: Int,
        date: String,
        startTime: String,
        endTime: String,
        matchName: String,
        participantIds: List<Int>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken().firstOrNull()
            if (token == null) {
                onError("로그인이 필요합니다")
                return@launch
            }

            val request = TeamMatchRegisterRequest(
                teamId = teamId,
                matchDate = date,
                startTime = startTime,
                endTime = endTime,
                matchName = matchName,
                participantList = participantIds
            )

            try {
                val response = RetrofitInstance.matchApi.registerTeamMatch("Bearer $token", request)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    onSuccess()
                } else {
                    onError(response.body()?.message ?: "등록 실패")
                }
            } catch (e: Exception) {
                onError("네트워크 오류: ${e.localizedMessage}")
            }
        }
    }

    /**
     * 경기 리포트를 생성하고 서버에 전송합니다.
     *
     * @param matchId 경기 ID
     * @param quarterData 쿼터 데이터 리스트
     */
    fun sendMatchReport(
        matchId: String,
        quarterData: List<MatchReportService.QuarterData>
    ) {
        viewModelScope.launch {
            _reportState.value = ReportState.Loading
            android.util.Log.d("MatchViewModel", "리포트 전송 시작: matchId=$matchId")

            try {
                // 경기장 모서리 좌표 확인
                val fieldCorners = matchRepository.getFieldCorners()
                if (fieldCorners == null) {
                    _reportState.value = ReportState.Error("경기장 좌표 데이터가 없습니다")
                    android.util.Log.e("MatchViewModel", "경기장 좌표 데이터가 없습니다")
                    return@launch
                }

                // 로그로 경기장 데이터 확인
                matchRepository.logFieldCorners()

                // 삼성 헬스 데이터 가져오기
                val exerciseData = samsungHealthService.getExercise()
                if (exerciseData.isEmpty()) {
                    _reportState.value = ReportState.Error("삼성 헬스에서 운동 데이터를 가져올 수 없습니다")
                    android.util.Log.e("MatchViewModel", "삼성 헬스 데이터가 없습니다")
                    return@launch
                }

                android.util.Log.d("MatchViewModel", "삼성 헬스 데이터 로드 성공: ${exerciseData.size}개")

                // 액세스 토큰 가져오기
                val token = tokenManager.getAccessToken().firstOrNull()
                if (token == null) {
                    _reportState.value = ReportState.Error("토큰이 없습니다")
                    return@launch
                }

                // 현재 날짜를 yyyy-MM-dd 형식으로 변환
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val matchDate = dateFormat.format(Date())

                // 리포트 데이터 생성
                val reportDataList = createReportDataList(exerciseData, quarterData, fieldCorners)

                // 요청 본문 생성
                val requestBody = JSONObject()
                requestBody.put("matchDate", matchDate)

                val reportArray = JSONArray()
                for (reportData in reportDataList) {
                    reportArray.put(reportData)
                }
                requestBody.put("reportDataList", reportArray)

                // 서버에 전송
                android.util.Log.d("MatchViewModel", "리포트 데이터 전송: $requestBody")

                try {
                    val response = matchApi.sendMatchReport(
                        matchId = matchId,
                        requestBody = requestBody,
                        token = "Bearer $token"
                    )

                    if (response.isSuccessful) {
                        _reportState.value = ReportState.Success
                        android.util.Log.d("MatchViewModel", "리포트 전송 성공")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _reportState.value = ReportState.Error("리포트 전송 실패: ${response.code()}")
                        android.util.Log.e("MatchViewModel", "리포트 전송 실패: code=${response.code()}, body=$errorBody")
                    }
                } catch (e: Exception) {
                    _reportState.value = ReportState.Error("네트워크 오류: ${e.message}")
                    android.util.Log.e("MatchViewModel", "리포트 전송 네트워크 오류", e)
                }

            } catch (e: Exception) {
                _reportState.value = ReportState.Error("리포트 생성 오류: ${e.message}")
                android.util.Log.e("MatchViewModel", "리포트 생성 오류", e)
            }
        }
    }

    /**
     * 리포트 데이터 목록 생성
     */
    private fun createReportDataList(
        exerciseList: List<com.ballog.mobile.data.model.Exercise>,
        quarterData: List<MatchReportService.QuarterData>,
        fieldCorners: List<GpsLocation>
    ): List<JSONObject> {
        val reportDataList = mutableListOf<JSONObject>()

        for (quarter in quarterData) {
            try {
                // 현재는 첫 번째 운동 데이터 사용 (시간 필터링 로직 추가 필요)
                val exercise = exerciseList.firstOrNull() ?: continue

                // 운동의 GPS 포인트를 경기장 내부 위치로 필터링
                val filteredPoints = exercise.gpsPoints.filter { point ->
                    val location = GpsLocation(point.latitude, point.longitude)
                    isPointInsideField(location, fieldCorners)
                }

                android.util.Log.d("MatchViewModel", "쿼터 ${quarter.quarterNumber}: 필터링된 GPS 포인트 ${filteredPoints.size}개")

                // 필터링된 GPS 포인트를 히트맵 그리드로 변환
                val heatmapGrid = createHeatmapGrid(filteredPoints, fieldCorners)

                // 쿼터 리포트 JSON 생성
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
                for (row in 0..9) {
                    val rowArray = JSONArray()
                    for (col in 0..15) {
                        rowArray.put(heatmapGrid[row][col])
                    }
                    heatmapArray.put(rowArray)
                }
                gameReportData.put("heatmap", heatmapArray)

                // gameReportData를 reportJson에 추가
                reportJson.put("gameReportData", gameReportData)

                reportDataList.add(reportJson)
            } catch (e: Exception) {
                android.util.Log.e("MatchViewModel", "쿼터 ${quarter.quarterNumber} 리포트 생성 오류", e)
            }
        }

        return reportDataList
    }

    /**
     * 점이 경기장 내부에 있는지 확인
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
     * GPS 포인트를 16x10 그리드로 변환
     */
    private fun createHeatmapGrid(
        gpsPoints: List<com.ballog.mobile.data.model.GpsPoint>,
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
     * 좌표를 그리드 위치로 변환
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
     * 그리드 값을 1-10 범위로 정규화
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
     * 히트맵 데이터를 로그로 출력합니다 (디버깅용)
     */
    fun logHeatMapData() {
        matchRepository.logHeatMapData()
        matchRepository.logFieldCorners()
        matchRepository.logNormalizedHeatMapData()
    }
}

/**
 * MatchItemDto → Domain Model로 변환
 */
fun MatchItemDto.toDomain(): Match {
    return Match(
        id = matchId,
        date = matchDate,
        startTime = startTime,
        endTime = endTime,
        matchName = matchName,
    )
}

/**
 * 달력 데이터를 생성하는 유틸 함수
 * @param currentMonth 현재 기준 월
 * @param matches 이 달에 등록된 매치 리스트
 * @return 7일 단위로 나눠진 달력 상태 리스트
 */
fun buildCalendar(currentMonth: LocalDate, matches: List<Match>): List<List<DateMarkerState>> {
    val yearMonth = YearMonth.from(currentMonth)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.withDayOfMonth(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek
    val dayOffset = (firstDayOfWeek.value % 7) // Sunday = 0

    val prevMonth = yearMonth.minusMonths(1)
    val prevMonthLength = prevMonth.lengthOfMonth()

    val totalCells = ((dayOffset + daysInMonth + 6) / 7) * 7
    val calendarDates = mutableListOf<DateMarkerState>()

    // ⬅ 앞달 날짜 추가
    for (i in dayOffset downTo 1) {
        val day = prevMonthLength - i + 1
        calendarDates.add(DateMarkerState(day.toString(), marked = false, selected = false, thisMonth = false))
    }

    // 📅 이번달 날짜 추가
    val matchDaySet = matches.mapNotNull { it.date.takeLast(2).toIntOrNull() }.toSet()
    for (day in 1..daysInMonth) {
        val marked = day in matchDaySet
        calendarDates.add(DateMarkerState(day.toString(), marked = marked, selected = false, thisMonth = true))
    }

    // ➡ 다음달 날짜 추가 (달력 빈칸 채우기용)
    val remaining = totalCells - calendarDates.size
    for (day in 1..remaining) {
        calendarDates.add(DateMarkerState(day.toString(), marked = false, selected = false, thisMonth = false))
    }

    return calendarDates.chunked(7) // 7일씩 한 주 단위로 자르기
}
