package com.ballog.mobile.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ballog.mobile.R
import com.ballog.mobile.data.model.Match
import com.ballog.mobile.data.model.MatchState
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.*
import com.ballog.mobile.ui.theme.BallogTheme
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.pretendard
import com.ballog.mobile.viewmodel.MatchViewModel
import com.ballog.mobile.viewmodel.buildCalendar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MatchScreen(navController: NavController, viewModel: MatchViewModel = viewModel()) {
    val today = remember { LocalDate.now() }
    var currentMonth by remember { mutableStateOf(today.withDayOfMonth(1)) }
    var selectedDate by remember { mutableStateOf(today) }
    val matchState by viewModel.matchState.collectAsState()
    val formattedMonth = currentMonth.format(DateTimeFormatter.ofPattern("yyyy년 M월"))
    val selectedDateStr = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    LaunchedEffect(currentMonth) {
        android.util.Log.d("MatchScreen", "📡 fetchMyMatches 요청: ${currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"))}")
        viewModel.fetchMyMatches(currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Gray.Gray100),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopNavItem(title = "매치", type = TopNavType.MAIN_BASIC)
        Spacer(modifier = Modifier.height(24.dp))

        when (matchState) {
            is MatchState.Loading -> {
                Text(
                    text = "불러오는 중...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard,
                    modifier = Modifier.padding(24.dp)
                )
            }
            is MatchState.Error -> {
                Text(
                    text = "에러: ${(matchState as MatchState.Error).message}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard,
                    modifier = Modifier.padding(24.dp)
                )
            }
            is MatchState.Success -> {
                val matches = (matchState as MatchState.Success).matches
                val calendarData = buildCalendar(currentMonth, matches).map { week ->
                    week.map { marker ->
                        marker.copy(selected = marker.date == selectedDate.dayOfMonth.toString() && marker.thisMonth)
                    }
                }

                MatchCalendar(
                    month = formattedMonth,
                    dates = calendarData,
                    onPrevMonth = { currentMonth = currentMonth.minusMonths(1) },
                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
                    onDateClick = { day -> selectedDate = day }
                )

                Spacer(modifier = Modifier.height(16.dp))

                val filteredMatches = matches.filter { it.date == selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (filteredMatches.isEmpty()) {
                        Text(
                            text = "경기 일정이 없습니다",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = pretendard
                        )
                    } else {
                        filteredMatches.forEach { match ->
                            MatchCard(
                                timeLabel = "경기 시간",
                                startTime = match.startTime,
                                endTime = match.endTime,
                                matchName = match.matchName,
                                onClick = {
                                    navController.navigate("match/detail/${match.id}")
                                }
                            )
                        }
                    }
                    BallogButton(
                        onClick = {
                            navController.navigate("match/register/${selectedDateStr}?mode=PERSONAL")
                        },
                        type = ButtonType.BOTH,
                        buttonColor = ButtonColor.GRAY,
                        icon = painterResource(id = R.drawable.ic_add),
                        label = "매치 등록"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MatchScreenPreview() {
    BallogTheme {
        val today = LocalDate.of(2025, 4, 22)
        var selectedDate = today
        val matches = listOf(
            Match(1, "2025-04-22", "20:00", "22:00", "강동 송파 풋살장"),
            Match(2, "2025-04-30", "18:00", "20:00", "잠실 풋살장")
        )
        val calendarData = buildCalendar(today.withDayOfMonth(1), matches).map { week ->
            week.map { marker ->
                marker.copy(selected = marker.date == selectedDate.dayOfMonth.toString() && marker.thisMonth)
            }
        }
        val filteredMatches = matches.filter { it.date == selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray.Gray100),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopNavItem(title = "팀", type = TopNavType.MAIN_BASIC)
            Spacer(modifier = Modifier.height(24.dp))

            MatchCalendar(
                month = "2025년 4월",
                dates = calendarData,
                onPrevMonth = {},
                onNextMonth = {},
                onDateClick = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (filteredMatches.isEmpty()) {
                    Text("경기 일정이 없습니다", modifier = Modifier.padding(bottom = 8.dp))
                } else {
                    filteredMatches.forEach { match ->
                        MatchCard(
                            timeLabel = "경기 시간",
                            startTime = match.startTime,
                            endTime = match.endTime,
                            matchName = match.matchName
                        )
                    }
                }
                BallogButton(
                    onClick = { /* TODO: navigate to match creation */ },
                    type = ButtonType.BOTH,
                    buttonColor = ButtonColor.GRAY,
                    icon = painterResource(id = R.drawable.ic_add),
                    label = "매치 등록"
                )
            }
        }
    }
}
