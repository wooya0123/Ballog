package com.ballog.mobile.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.theme.BallogTheme

@Composable
fun LikedVideosScreen() {
    val dummyList = listOf(
        LikedVideoItem("1", "2025.05.08 15:00", "강동 송파 풋살장 어쩌구저쩌구쏼라쏼라", "하이라이트 1"),
        LikedVideoItem("2", "2025.05.08 15:00", "강동 송파 풋살장", "하이라이트 2"),
        LikedVideoItem("3", "2025.05.08 15:00", "강동 송파 풋살장", "하이라이트 3")
    )

    Column {
        TopNavItem(
            title = "좋아요한 영상",
            type = TopNavType.DETAIL_WITH_BACK,
            onBackClick = { /* navController.popBackStack() 예정 */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dummyList) { video ->
                LikedHighlightCard(video = video)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LikedVideosScreenPreview() {
    BallogTheme {
        LikedVideosScreen()
    }
}
