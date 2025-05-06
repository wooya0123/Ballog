package com.ballog.mobile.ui.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.ui.theme.System
import com.ballog.mobile.ui.theme.pretendard

@Composable
fun TeamDelegateScreen(
    navController: NavController,
    teamName: String,
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    var selectedMember by remember { mutableStateOf<String?>(null) }
    val members = listOf("김나현", "김용현", "양창민", "전승현", "최현우")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        TopNavItem(
            title = "권한 위임",
            type = TopNavType.DETAIL_WITH_BACK_CLOSE,
            onBackClick = onBackClick,
            onActionClick = onCloseClick
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            items(members) { member ->
                MemberItem(
                    name = member,
                    isSelected = selectedMember == member,
                    onClick = { selectedMember = member }
                )
            }
        }

        // Save Button
        BallogButton(
            onClick = onSaveClick,
            type = ButtonType.LABEL_ONLY,
            buttonColor = ButtonColor.BLACK,
            label = "저장하기",
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun MemberItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Gray.Gray200 else Gray.Gray100
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Primary else Gray.Gray800,
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
fun TeamDelegateScreenPreview() {
    TeamDelegateScreen(
        navController = rememberNavController(),
        teamName = "FS 핑크팬서"
    )
}

@Preview(showBackground = true, name = "Selected Member Preview")
@Composable
fun TeamDelegateScreenWithSelectionPreview() {
    var selectedMember by remember { mutableStateOf("김나현") }
    val members = listOf("김나현", "김용현", "양창민", "전승현", "최현우")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        TopNavItem(
            title = "권한 위임",
            type = TopNavType.DETAIL_WITH_BACK_CLOSE
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            items(members) { member ->
                MemberItem(
                    name = member,
                    isSelected = selectedMember == member,
                    onClick = { selectedMember = member }
                )
            }
        }

        BallogButton(
            onClick = { },
            type = ButtonType.LABEL_ONLY,
            buttonColor = ButtonColor.BLACK,
            label = "저장하기",
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        )
    }
}
