package com.ballog.mobile.ui.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ballog.mobile.R
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.PlayerCard
import com.ballog.mobile.ui.components.TabMenu
import com.ballog.mobile.ui.components.TeamInfoCard
import com.ballog.mobile.ui.components.TeamStats
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.pretendard

@Composable
fun TeamDetailScreen(
    navController: NavController,
    teamName: String = "FS 핑크팬서",
    onBackClick: () -> Unit = {},
    onSettingClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        // Top Navigation Bar with Settings
        TopNavItem(
            title = teamName,
            type = TopNavType.DETAIL_WITH_BACK_SETTINGS,
            onBackClick = onBackClick,
            onActionClick = onSettingClick
        )
        
        // Tab Menu
        TabMenu(
            leftTabText = "팀 정보",
            rightTabText = "매치",
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        // Content based on selected tab
        when (selectedTab) {
            0 -> TeamInfoTab()
            1 -> TeamMatchTab()
        }
    }
}

@Composable
private fun TeamInfoTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            TeamInfoCard(
                stats = TeamStats(
                    attack = 65,
                    defence = 60,
                    speed = 45,
                    recovery = 60,
                    stamina = 70
                )
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "멤버 수",
                    tint = Gray.Gray800,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "6",
                    fontSize = 12.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    color = Gray.Gray800
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(samplePlayers) { player ->
            PlayerCard(
                name = player.name,
                isManager = player.isManager
            )
        }
    }
}

@Composable
private fun TeamMatchTab() {
    // TODO: Implement match tab
}

// Sample data
private data class Player(
    val name: String,
    val isManager: Boolean = false
)

private val samplePlayers = listOf(
    Player("김가희", true),
    Player("이지민"),
    Player("박서준"),
    Player("최유진"),
    Player("정민수"),
    Player("한소희")
)

@Preview(showBackground = true)
@Composable
fun TeamDetailScreenPreview() {
    TeamDetailScreen(rememberNavController())
}
