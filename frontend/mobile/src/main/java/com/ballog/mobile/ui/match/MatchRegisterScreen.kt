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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.DropDown
import com.ballog.mobile.ui.components.Input
import com.ballog.mobile.ui.theme.BallogTheme
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.ui.theme.pretendard

@Composable
fun MatchRegisterScreen() {
    val locations = listOf("강동 송파 풋살장", "잠실 풋살장", "기타 구장")
    var selectedLocation by remember { mutableStateOf(locations[0]) }
    var locationDropdownExpanded by remember { mutableStateOf(false) }

    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }

    val players = listOf("김가희", "이철수", "박영희", "최민수", "홍길동")
    var selectedPlayers by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopNavItem(title = "매치 등록", type = TopNavType.DETAIL_WITH_BACK)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "어디서 매치 예정이신가요?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = pretendard,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        DropDown(
            items = locations,
            selectedItem = selectedLocation,
            onItemSelected = { selectedLocation = it },
            expanded = locationDropdownExpanded,
            onExpandedChange = { locationDropdownExpanded = it },
            modifier = Modifier
                .padding(horizontal = 24.dp)
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
                onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) hour = it },
                placeholder = "00",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                modifier = Modifier.weight(1f)
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
                onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) minute = it },
                placeholder = "00",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }
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

                        // 구분선 (마지막 항목엔 생략 가능)
                        if (index < players.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Gray.Gray300)
                                    .padding(start = 48.dp) // 체크박스 영역 제외하고 시작하려면
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BallogButton(
            onClick = { /* TODO */ },
            type = ButtonType.LABEL_ONLY,
            buttonColor = ButtonColor.BLACK,
            label = "저장하기",
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MatchRegisterScreenPreview() {
    BallogTheme {
        MatchRegisterScreen()
    }
}
