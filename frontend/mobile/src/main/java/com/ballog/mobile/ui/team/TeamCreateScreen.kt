package com.ballog.mobile.ui.team

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ballog.mobile.R
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.theme.pretendard

@Composable
fun TeamCreateScreen(
    onNavigateBack: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        // Top Navigation Bar
        TopNavItem(
            type = TopNavType.DETAIL_WITH_BACK,
            title = "팀 생성",
            onBackClick = onNavigateBack,
            onActionClick = onClose
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            // Logo Upload Area
            Box(
                modifier = Modifier
                    .size(146.dp)
                    .background(Gray.Gray200, RoundedCornerShape(8.dp))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add logo",
                        tint = Gray.Gray500
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "로고 이미지",
                        fontSize = 16.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray.Gray500
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Team Name Input
            Text(
                text = "팀명",
                fontSize = 14.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                color = Gray.Gray800
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Gray.Gray200, RoundedCornerShape(8.dp))
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = {
                        Text(
                            text = "팀명",
                            fontFamily = pretendard,
                            color = Gray.Gray500,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Gray.Gray200,
                        focusedContainerColor = Gray.Gray200,
                        unfocusedIndicatorColor = Gray.Gray200,
                        focusedIndicatorColor = Gray.Gray200
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Foundation Date
            Text(
                text = "창단일자",
                fontSize = 14.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                color = Gray.Gray800
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("YYYY", "MM", "DD").forEach { placeholder ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .background(Gray.Gray200, RoundedCornerShape(8.dp))
                    ) {
                        TextField(
                            value = "",
                            onValueChange = {},
                            placeholder = {
                                Text(
                                    text = placeholder,
                                    fontFamily = pretendard,
                                    color = Gray.Gray500,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxSize(),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Gray.Gray200,
                                focusedContainerColor = Gray.Gray200,
                                unfocusedIndicatorColor = Gray.Gray200,
                                focusedIndicatorColor = Gray.Gray200
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 14.sp,
                                textAlign = TextAlign.Start
                            ),
                            singleLine = true
                        )
                    }
                    if (placeholder != "DD") {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Invite Link
            Text(
                text = "멤버 초대 링크",
                fontSize = 14.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                color = Gray.Gray800
            )
            Text(
                text = "팀에 초대하고 싶은 분에게 링크를 전달해주세요!",
                fontSize = 12.sp,
                fontFamily = pretendard,
                color = Gray.Gray500
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .background(Gray.Gray200, RoundedCornerShape(8.dp))
                ) {
                    TextField(
                        value = "https://ballog.page.link/team-invite?code=abc123",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxSize(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Gray.Gray200,
                            focusedContainerColor = Gray.Gray200,
                            unfocusedIndicatorColor = Gray.Gray200,
                            focusedIndicatorColor = Gray.Gray200
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            fontFamily = pretendard,
                            textAlign = TextAlign.Start
                        ),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { /* TODO: Implement copy functionality */ },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Gray.Gray100, RoundedCornerShape(8.dp))
                        .border(1.dp, Gray.Gray300, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_copy),
                        contentDescription = "Copy link",
                        tint = Gray.Gray600

                    )
                }
            }
        }

        // Save Button - Positioned at the bottom
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Button(
                onClick = { /* TODO: Implement save functionality */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gray.Gray700
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "생성하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary
                )
            }
        }
    }
}

@Preview(
    name = "팀 생성 화면",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun TeamCreateScreenPreview() {
    TeamCreateScreen()
}
