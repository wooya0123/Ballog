package com.ballog.mobile.ui.match

import android.util.Log
import androidx.compose.runtime.Composable
import com.ballog.mobile.data.model.Match
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import com.ballog.mobile.ui.components.DropDown
import com.ballog.mobile.ui.components.SectionHeader
import com.ballog.mobile.ui.components.PlayerCard
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

private const val TAG = "MatchReportTab"

@Composable
fun MatchReportTab(match: Match) {
    // DropDown 상태
    var selectedQuarter by remember { mutableStateOf("1 쿼터") }
    var dropDownExpanded by remember { mutableStateOf(false) }

    // SectionHeader 상태
    var myRecordExpanded by remember { mutableStateOf(true) }
    var playerListExpanded by remember { mutableStateOf(true) }

    // 샘플 데이터
    val heatMapData = List(15) { List(10) { (0..5).random() } }
    val reportCards = listOf(
        Triple("분석시간", "29.47", "분"),
        Triple("이동거리", "2.87", "km"),
        Triple("최고 속도", "22.79", "km/h"),
        Triple("평균 속도", "5.82", "km/h"),
        Triple("스프린트", "13", "회"),
        Triple("활동 범위", "62.04", "%"),
        Triple("스프린트거리", "625.18", "m"),
        Triple("어질리티", "16.18", "%")
    )
    val playerList = listOf(
        Pair("김가희", true),
        Pair("김가희", false)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // DropDown
        DropDown(
            items = listOf("1 쿼터", "2 쿼터", "3 쿼터", "4 쿼터"),
            selectedItem = selectedQuarter,
            onItemSelected = { selectedQuarter = it },
            expanded = dropDownExpanded,
            onExpandedChange = { dropDownExpanded = it },
            modifier = Modifier
        )
        // HeatMap
        HeatMap(heatData = heatMapData, modifier = Modifier)
        // 내 기록 Section
        SectionHeader(
            title = "내 기록",
            isExpanded = myRecordExpanded,
            onToggle = { myRecordExpanded = !myRecordExpanded },
            modifier = Modifier
        )
        if (myRecordExpanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reportCards.forEach { (label, value, unit) ->
                    MatchReportCard(label = label, value = value, unit = unit)
                }
            }
        }
        // 참여 인원 Section
        SectionHeader(
            title = "참여 인원",
            isExpanded = playerListExpanded,
            onToggle = { playerListExpanded = !playerListExpanded },
            modifier = Modifier
        )
        if (playerListExpanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                playerList.forEach { (name, isManager) ->
                    PlayerCard(name = name, isManager = isManager)
                }
            }
        }
    }
    Log.d(TAG, "$match")
}
