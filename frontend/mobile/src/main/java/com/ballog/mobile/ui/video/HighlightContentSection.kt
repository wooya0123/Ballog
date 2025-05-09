package com.ballog.mobile.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ballog.mobile.R
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.DropDown
import com.ballog.mobile.ui.theme.Gray

@Composable
fun HighlightContentSection(
    highlights: List<HighlightUiState>,
    selectedQuarter: String,
    expanded: Boolean,
    onQuarterChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (HighlightUiState) -> Unit,
    onDeleteVideo: () -> Unit
) {
    Column {
        // 공통: 영상 자리
        VideoPlaceholderBox()

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(20.dp))

            QuarterDropDown(
                selectedQuarter = selectedQuarter,
                expanded = expanded,
                onQuarterChange = onQuarterChange,
                onExpandedChange = onExpandedChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (highlights.isEmpty()) {
                BallogButton(
                    onClick = { /* TODO: 영상 업로드 */ },
                    type = ButtonType.BOTH,
                    buttonColor = ButtonColor.GRAY,
                    icon = painterResource(id = R.drawable.ic_upload),
                    label = "영상 업로드",
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )
            } else {
                highlights.forEach { highlight ->
                    HighlightCard(
                        title = highlight.title,
                        startTime = "${highlight.startHour}:${highlight.startMin}",
                        endTime = "${highlight.endHour}:${highlight.endMin}",
                        onEdit = { onEditClick(highlight) },
                        onLike = { /* TODO */ }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                BallogButton(
                    onClick = onAddClick,
                    type = ButtonType.BOTH,
                    buttonColor = ButtonColor.GRAY,
                    icon = painterResource(id = R.drawable.ic_add),
                    label = "구간 추가",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    color = Gray.Gray300,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                BallogButton(
                    onClick = onDeleteVideo,
                    type = ButtonType.BOTH,
                    buttonColor = ButtonColor.ALERT,
                    icon = painterResource(id = R.drawable.ic_trash),
                    label = "구간 삭제",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun VideoPlaceholderBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Gray.Gray300)
    )
}

@Composable
private fun QuarterDropDown(
    selectedQuarter: String,
    expanded: Boolean,
    onQuarterChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    DropDown(
        items = listOf("1 쿼터", "2 쿼터", "3 쿼터", "4 쿼터"),
        selectedItem = selectedQuarter,
        onItemSelected = onQuarterChange,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    )
}
