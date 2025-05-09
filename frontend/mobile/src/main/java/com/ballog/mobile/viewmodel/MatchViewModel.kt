package com.ballog.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ballog.mobile.BallogApplication
import com.ballog.mobile.data.api.RetrofitInstance
import com.ballog.mobile.data.dto.MatchItemDto
import com.ballog.mobile.data.dto.MatchRegisterRequest
import com.ballog.mobile.data.model.Match
import com.ballog.mobile.data.model.MatchState
import com.ballog.mobile.ui.components.DateMarkerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class MatchViewModel : ViewModel() {
    private val tokenManager = BallogApplication.getInstance().tokenManager
    private val matchApi = RetrofitInstance.matchApi

    // 매치 상태 (로딩 / 성공 / 에러)
    private val _matchState = MutableStateFlow<MatchState>(MatchState.Loading)
    val matchState: StateFlow<MatchState> = _matchState

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
