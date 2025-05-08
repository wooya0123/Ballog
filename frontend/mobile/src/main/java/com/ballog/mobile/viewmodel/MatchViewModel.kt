package com.ballog.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ballog.mobile.BallogApplication
import com.ballog.mobile.data.api.RetrofitInstance
import com.ballog.mobile.data.dto.MatchItemDto
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

    private val _matchState = MutableStateFlow<MatchState>(MatchState.Loading)
    val matchState: StateFlow<MatchState> = _matchState

    fun fetchMyMatches(month: String) {
        viewModelScope.launch {
            _matchState.value = MatchState.Loading
            try {
                val token = tokenManager.getAccessToken().firstOrNull() ?: return@launch
                val response = matchApi.getMyMatches("Bearer $token", month)
                val body = response.body()

                android.util.Log.d("MatchViewModel", "✅ 응답 결과: ${body}")

                if (response.isSuccessful && body?.isSuccess == true) {
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
}

fun MatchItemDto.toDomain(): Match {
    return Match(
        id = matchId,
        date = matchDate,
        startTime = startTime,
        endTime = endTime,
        location = location
    )
}

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

    // 앞달 날짜 추가
    for (i in dayOffset downTo 1) {
        val day = prevMonthLength - i + 1
        calendarDates.add(DateMarkerState(day.toString(), marked = false, selected = false, thisMonth = false))
    }

    // 이번달 날짜 추가
    val matchDaySet = matches.mapNotNull { it.date.takeLast(2).toIntOrNull() }.toSet()
    for (day in 1..daysInMonth) {
        val marked = day in matchDaySet
        calendarDates.add(DateMarkerState(day.toString(), marked = marked, selected = false, thisMonth = true))
    }

    // 다음달 날짜 추가
    val remaining = totalCells - calendarDates.size
    for (day in 1..remaining) {
        calendarDates.add(DateMarkerState(day.toString(), marked = false, selected = false, thisMonth = false))
    }

    return calendarDates.chunked(7)
}
