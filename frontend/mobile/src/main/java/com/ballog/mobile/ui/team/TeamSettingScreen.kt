package com.ballog.mobile.ui.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.System
import com.ballog.mobile.ui.theme.pretendard

@Composable
fun TeamSettingScreen(
    navController: NavController,
    teamName: String,
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onDelegateClick: () -> Unit = {},
    onKickMemberClick: () -> Unit = {},
    onInviteLinkClick: () -> Unit = {},
    onDeleteTeamClick: () -> Unit = {},
    onLeaveTeamClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        TopNavItem(
            title = "팀 설정",
            type = TopNavType.DETAIL_WITH_BACK_CLOSE,
            onBackClick = onBackClick,
            onActionClick = onCloseClick
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            SettingMenuItem(
                text = "권한 위임",
                onClick = onDelegateClick
            )
            
            SettingMenuItem(
                text = "멤버 강제 퇴장",
                onClick = onKickMemberClick
            )
            
            SettingMenuItem(
                text = "멤버 초대 링크",
                onClick = onInviteLinkClick
            )
            
            SettingMenuItem(
                text = "팀 삭제",
                onClick = onDeleteTeamClick,
                isWarning = true
            )
            
            SettingMenuItem(
                text = "팀 탈퇴",
                onClick = onLeaveTeamClick,
                isWarning = true
            )
        }
    }
}

@Composable
private fun SettingMenuItem(
    text: String,
    onClick: () -> Unit,
    isWarning: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Medium,
                    color = if (isWarning) System.Red else Gray.Gray800,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            Divider(
                color = Gray.Gray200,
                thickness = 1.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TeamSettingScreenPreview() {
    TeamSettingScreen(
        navController = rememberNavController(),
        teamName = "FS 핑크팬서"
    )
}
