package com.ballog.mobile.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.ballog.mobile.navigation.Routes
import com.ballog.mobile.ui.components.NavigationBar
import com.ballog.mobile.ui.auth.HomeScreen
import com.ballog.mobile.ui.match.MatchScreen
import com.ballog.mobile.ui.team.TeamListScreen
import com.ballog.mobile.ui.profile.MyPageScreen
import androidx.compose.material3.Scaffold
import com.ballog.mobile.ui.components.NavigationTab

@Composable
fun MainScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                },
                onActionClick = {
                    // 중앙 버튼 눌렀을 때 동작
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                NavigationTab.HOME -> HomeScreen()
                NavigationTab.MATCH -> MatchScreen()
                NavigationTab.TEAM -> TeamListScreen(navController=navController)
                NavigationTab.MYPAGE -> MyPageScreen(navController=navController)
            }
        }
    }
}
