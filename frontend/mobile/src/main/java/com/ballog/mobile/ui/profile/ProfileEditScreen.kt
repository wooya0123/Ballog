package com.ballog.mobile.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ballog.mobile.R
import com.ballog.mobile.navigation.TopNavItem
import com.ballog.mobile.navigation.TopNavType
import com.ballog.mobile.ui.components.*
import com.ballog.mobile.ui.theme.*
import androidx.compose.ui.graphics.Color

@Composable
fun ProfileEditScreen(navController: NavController) {
    val context = LocalContext.current
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    var nickname by remember { mutableStateOf("찐쨔시") }
    var birthYear by remember { mutableStateOf("2000") }
    var birthMonth by remember { mutableStateOf("11") }
    var birthDay by remember { mutableStateOf("18") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray.Gray100)
    ) {
        TopNavItem(
            title = "정보 수정",
            type = TopNavType.DETAIL_WITH_BACK,
            onBackClick = { navController.popBackStack() },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 프로필 이미지
            Box(
                modifier = Modifier
                    .size(146.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF2F5F8))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                profileImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "프로필 이미지",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "프로필 선택",
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 닉네임 라벨
            Text(
                text = "닉네임",
                fontSize = 14.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            Input(
                value = nickname,
                onValueChange = { nickname = it },
                placeholder = "닉네임",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 생년월일 라벨
            Text(
                text = "생년월일",
                fontSize = 14.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

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

            // 저장 버튼
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
