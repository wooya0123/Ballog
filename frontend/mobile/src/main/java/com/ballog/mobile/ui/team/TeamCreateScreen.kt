package com.ballog.mobile.ui.team

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ballog.mobile.R
import com.ballog.mobile.ui.theme.Gray
import com.ballog.mobile.ui.theme.Primary
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.theme.pretendard
import com.ballog.mobile.viewmodel.TeamViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun TeamCreateScreen(
    teamViewModel: TeamViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    var teamName by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    
    val isLoading by teamViewModel.isLoading.collectAsState()
    val error by teamViewModel.error.collectAsState()
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    fun validateForm(): Boolean {
        if (teamName.isBlank()) {
            teamViewModel.setError("팀명을 입력해주세요")
            return false
        }
        if (year.isBlank() || month.isBlank() || day.isBlank()) {
            teamViewModel.setError("창단일자를 모두 입력해주세요")
            return false
        }
        return true
    }

    fun handleCreateTeam() {
        if (!validateForm()) return
        
        coroutineScope.launch {
            val foundationDate = "$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}"
            val logoImageUrl = "" // TODO: Implement image upload
            
            teamViewModel.addTeam(teamName, logoImageUrl, foundationDate)
                .onSuccess {
                    onNavigateBack()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // Top Navigation Bar
        TopNavItem(
            type = TopNavType.DETAIL_WITH_BACK,
            title = "팀 생성",
            onBackClick = onNavigateBack,
            onActionClick = onClose
        )

        // Error Message
        if (error != null) {
            Text(
                text = error ?: "",
                color = Primary,
                fontSize = 12.sp,
                fontFamily = pretendard,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
                .windowInsetsPadding(WindowInsets.ime)
        ) {
            // Logo Upload Area
            Box(
                modifier = Modifier
                    .size(146.dp)
                    .background(Gray.Gray200, RoundedCornerShape(8.dp))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Gray.Gray500,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
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
                    value = teamName,
                    onValueChange = { teamName = it },
                    placeholder = {
                        Text(
                            text = "팀명",
                            fontFamily = pretendard,
                            color = Gray.Gray500,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
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

            // Foundation Date Input
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Year Input
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .background(Gray.Gray200, RoundedCornerShape(8.dp))
                ) {
                    TextField(
                        value = year,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                if (newValue.length <= 4) {
                                    val yearNum = newValue.toIntOrNull() ?: 0
                                    if (yearNum <= 9999) {
                                        year = newValue
                                    }
                                }
                            }
                        },
                        placeholder = {
                            Text(
                                text = "년",
                                fontFamily = pretendard,
                                color = Gray.Gray500,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // Month Input
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .background(Gray.Gray200, RoundedCornerShape(8.dp))
                ) {
                    TextField(
                        value = month,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                if (newValue.length <= 2) {
                                    val monthNum = newValue.toIntOrNull() ?: 0
                                    if (monthNum <= 12) {
                                        month = newValue
                                    }
                                }
                            }
                        },
                        placeholder = {
                            Text(
                                text = "월",
                                fontFamily = pretendard,
                                color = Gray.Gray500,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // Day Input
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .background(Gray.Gray200, RoundedCornerShape(8.dp))
                ) {
                    TextField(
                        value = day,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                if (newValue.length <= 2) {
                                    val dayNum = newValue.toIntOrNull() ?: 0
                                    if (dayNum <= 31) {
                                        day = newValue
                                    }
                                }
                            }
                        },
                        placeholder = {
                            Text(
                                text = "일",
                                fontFamily = pretendard,
                                color = Gray.Gray500,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        // Save Button - Positioned at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .windowInsetsPadding(WindowInsets.ime)
        ) {
            BallogButton(
                onClick = { handleCreateTeam() },
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.PRIMARY,
                label = "팀 생성하기",
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
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
