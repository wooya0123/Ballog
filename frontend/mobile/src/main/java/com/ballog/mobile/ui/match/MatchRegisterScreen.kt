package com.ballog.mobile.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.*
import com.ballog.mobile.ui.theme.*
import com.ballog.mobile.viewmodel.MatchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

enum class MatchRegisterMode { PERSONAL, TEAM }

@Composable
fun MatchRegisterScreen(
    mode: MatchRegisterMode,
    navController: NavController,
    selectedDate: String,
    viewModel: MatchViewModel = viewModel()
) {
    val isTeam = mode == MatchRegisterMode.TEAM
    val stadiumList by viewModel.stadiumList.collectAsState()
    var selectedLocation by remember { mutableStateOf("") }
    var locationDropdownExpanded by remember { mutableStateOf(false) }

    val hourFocus = remember { FocusRequester() }
    val minuteFocus = remember { FocusRequester() }
    val endHourFocus = remember { FocusRequester() }
    val endMinuteFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var endHour by remember { mutableStateOf("") }
    var endMinute by remember { mutableStateOf("") }
    val players = listOf("김가희", "이철수", "박영희", "최민수", "홍길동")
    var selectedPlayers by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) { viewModel.fetchStadiumList() }
    LaunchedEffect(stadiumList) {
        if (stadiumList.isNotEmpty() && selectedLocation.isBlank()) {
            selectedLocation = stadiumList.first()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        TopNavItem(
            type = TopNavType.DETAIL_WITH_BACK,
            title = "매치 등록",
            onBackClick = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "어디서 매치 예정이신가요?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = pretendard,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        DropDown(
            items = stadiumList,
            selectedItem = selectedLocation,
            onItemSelected = { selectedLocation = it },
            expanded = locationDropdownExpanded,
            onExpandedChange = { locationDropdownExpanded = it },
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "매치 시작 시간을 알려주세요!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = pretendard,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Input(
                value = hour,
                onValueChange = {
                    if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                        hour = it
                        if (it.length == 2) minuteFocus.requestFocus()
                    }
                },
                placeholder = "00",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                modifier = Modifier.weight(1f).focusRequester(hourFocus)
            )
            Text(
                text = ":",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Input(
                value = minute,
                onValueChange = {
                    if (it.length <= 2 && it.all { c -> c.isDigit() }) minute = it
                },
                placeholder = "00",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                modifier = Modifier.weight(1f).focusRequester(minuteFocus)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "매치 종료 시간을 알려주세요!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = pretendard,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Input(
                value = endHour,
                onValueChange = {
                    if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                        endHour = it
                        if (it.length == 2) endMinuteFocus.requestFocus()
                    }
                },
                placeholder = "00",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                modifier = Modifier.weight(1f).focusRequester(endHourFocus)
            )
            Text(
                text = ":",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Input(
                value = endMinute,
                onValueChange = {
                    if (it.length <= 2 && it.all { c -> c.isDigit() }) endMinute = it
                },
                placeholder = "00",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                modifier = Modifier.weight(1f).focusRequester(endMinuteFocus)
            )
        }

        if (isTeam) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "같이 뛸 사람들을 골라주세요!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .background(Gray.Gray200, RoundedCornerShape(8.dp))
            ) {
                Column {
                    players.forEachIndexed { index, player ->
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = selectedPlayers.contains(player),
                                    onCheckedChange = {
                                        selectedPlayers = if (it) selectedPlayers + player else selectedPlayers - player
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Gray.Gray800,
                                        checkmarkColor = Primary,
                                        uncheckedColor = Gray.Gray400
                                    )
                                )
                                Text(
                                    text = player,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = pretendard
                                )
                            }
                            if (index < players.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Gray.Gray300)
                                        .padding(start = 48.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BallogButton(
            onClick = {
                if (isTeam) {
                    // TODO: 팀 매치 등록 로직
                } else {
                    if (
                        selectedLocation.isBlank() || hour.isBlank() || minute.isBlank() ||
                        endHour.isBlank() || endMinute.isBlank()
                    ) {
                        println("⚠️ 입력값이 부족합니다.")
                        return@BallogButton
                    }

                    val date = selectedDate
                    val startTime = "${hour.padStart(2, '0')}:${minute.padStart(2, '0')}"
                    val endTime = "${endHour.padStart(2, '0')}:${endMinute.padStart(2, '0')}"
                    val stadiumId = "1" // TODO: stadiumName → stadiumId 매핑 필요

                    viewModel.registerMyMatch(
                        date = date,
                        startTime = startTime,
                        endTime = endTime,
                        stadiumId = stadiumId,
                        onSuccess = {
                            println("✅ 매치 등록 성공")
                            navController.popBackStack()
                        },
                        onError = { error ->
                            println("❌ 매치 등록 실패: $error")
                        }
                    )
                }
            },
            type = ButtonType.LABEL_ONLY,
            buttonColor = ButtonColor.BLACK,
            label = "저장하기",
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        )
    }
}
