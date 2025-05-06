package com.ballog.mobile.ui.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ballog.mobile.navigation.Routes
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.TeamCard
import com.ballog.mobile.ui.components.TeamInfo
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.pretendard

@Composable
fun TeamListScreen(
    navController: NavController,
    onTeamClick: (String) -> Unit = { teamName ->
        navController.navigate("team/detail/$teamName")
    }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        TopNavItem(
            title = "팀",
            type = TopNavType.MAIN_WITH_CREATE,
            navController = navController,
            onActionClick = {
                navController.navigate(Routes.TEAM_CREATE)
            }
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(sampleTeams) { team ->
                TeamCard(
                    team = team,
                    onClick = { onTeamClick(team.name) }
                )
            }
        }

        Text(
            text = "팀 목록",
            fontSize = 20.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight.SemiBold,
            color = Gray.Gray800
        )
        
        Text(
            text = "소속된 팀이 없습니다.",
            fontSize = 14.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            color = Gray.Gray500
        )
    }
}

// 샘플 데이터
private val sampleTeams = listOf(
    TeamInfo(
        name = "FS 핑크팬서",
        foundingDate = "2023.08.14",
        imageUrl = "https://picsum.photos/200"
    ),
    TeamInfo(
        name = "FS 핑크팬서",
        foundingDate = "2023.08.14",
        imageUrl = "https://picsum.photos/200"
    ),
    TeamInfo(
        name = "FS 핑크팬서",
        foundingDate = "2023.08.14",
        imageUrl = "https://picsum.photos/200"
    ),
    TeamInfo(
        name = "FS 핑크팬서",
        foundingDate = "2023.08.14",
        imageUrl = "https://picsum.photos/200"
    ),
    TeamInfo(
        name = "FS 핑크팬서",
        foundingDate = "2023.08.14",
        imageUrl = "https://picsum.photos/200"
    )
)

@Preview(showBackground = true)
@Composable
fun TeamListScreenPreview() {
    TeamListScreen(rememberNavController())
} 
