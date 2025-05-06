package com.ballog.mobile.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.BallogButton
import com.ballog.mobile.ui.components.ButtonColor
import com.ballog.mobile.ui.components.ButtonType
import com.ballog.mobile.ui.components.Input
import com.ballog.mobile.ui.theme.*
import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.material3.*

@Composable
fun ProfileEditScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        TopNavItem(
            title = "정보 수정",
            type = TopNavType.DETAIL_WITH_BACK,
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 프로필 이미지 자리
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Gray.Gray200)
            )

            Spacer(modifier = Modifier.height(32.dp))

            var nickname by remember { mutableStateOf("찐쨔시") }
            var birthYear by remember { mutableStateOf("2000") }
            var birthMonth by remember { mutableStateOf("11") }
            var birthDay by remember { mutableStateOf("18") }

            Input(
                value = nickname,
                onValueChange = { nickname = it },
                placeholder = "닉네임",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Input(
                    value = birthYear,
                    onValueChange = { birthYear = it },
                    placeholder = "년",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                Input(
                    value = birthMonth,
                    onValueChange = { birthMonth = it },
                    placeholder = "월",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                Input(
                    value = birthDay,
                    onValueChange = { birthDay = it },
                    placeholder = "일",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            BallogButton(
                onClick = { navController.popBackStack() },
                type = ButtonType.LABEL_ONLY,
                buttonColor = ButtonColor.BLACK,
                label = "저장",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileEditScreenPreview() {
    ProfileEditScreen(navController = rememberNavController())
}
