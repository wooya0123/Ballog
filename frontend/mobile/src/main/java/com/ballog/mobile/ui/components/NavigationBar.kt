package com.ballog.mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.R
import com.ballog.mobile.ui.theme.pretendard

enum class NavigationTab { HOME, MATCH, TEAM, MYPAGE, DATA }

@Composable
fun NavigationBar(
    selectedTab: NavigationTab = NavigationTab.HOME,
    onTabSelected: (NavigationTab) -> Unit = {},
    onActionClick: () -> Unit = {}
) {
    val activeColor = Gray.Gray800
    val inactiveColor = Gray.Gray400
    val backgroundColor = Gray.Gray100
    val borderColor = Gray.Gray300

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .background(backgroundColor)
                .border(BorderStroke(1.dp, borderColor))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavigationBarTab(
                    iconRes = R.drawable.ic_home,
                    label = "홈",
                    selected = selectedTab == NavigationTab.HOME,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onTabSelected(NavigationTab.HOME) }
                )
                NavigationBarTab(
                    iconRes = R.drawable.ic_calendar,
                    label = "매치",
                    selected = selectedTab == NavigationTab.MATCH,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onTabSelected(NavigationTab.MATCH) }
                )
                Spacer(modifier = Modifier.width(48.dp))
                NavigationBarTab(
                    iconRes = R.drawable.ic_team,
                    label = "팀",
                    selected = selectedTab == NavigationTab.TEAM,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onTabSelected(NavigationTab.TEAM) }
                )
                NavigationBarTab(
                    iconRes = R.drawable.ic_profile,
                    label = "마이페이지",
                    selected = selectedTab == NavigationTab.MYPAGE,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onTabSelected(NavigationTab.MYPAGE) }
                )
            }
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.TopCenter)
                .offset(y = 8.dp)
                .clip(CircleShape)
                .background(activeColor)
                .clickable { onTabSelected(NavigationTab.DATA) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_watch),
                contentDescription = "Action",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun NavigationBarTab(
    iconRes: Int,
    label: String,
    selected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    val color = if (selected) activeColor else inactiveColor
    Column(
        modifier = Modifier
            .width(56.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = pretendard
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNavigationBar() {
    NavigationBar(
        selectedTab = NavigationTab.HOME,
        onTabSelected = {},
        onActionClick = {}
    )
}
