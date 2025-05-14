package com.ballog.mobile.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ballog.mobile.R
import com.ballog.mobile.ui.components.PlayerCard
import androidx.compose.ui.platform.LocalDensity
import com.ballog.mobile.ui.components.CardFace

@Composable
fun PlayerCardDialog(
    name: String,
    stats: List<Pair<String, String>>,
    onDismiss: () -> Unit
) {
    // 상태 변수: 다이얼로그가 처음 열릴 때 회전을 시작하기 위해 사용
    var flipped by remember { mutableStateOf(false) }

    // 회전 애니메이션: flipped 값이 true일 때 0 → 180도로 회전
    val rotationY = remember { Animatable(0f) }

    // 다이얼로그가 화면에 나타났을 때 자동으로 flipped → true로 설정해서 회전 시작
    LaunchedEffect(Unit) {
        flipped = true
    }

    LaunchedEffect(Unit) {
        rotationY.animateTo(
            targetValue = 1080f, // 3바퀴
            animationSpec = tween(
                durationMillis = 2000,
                easing = {
                    // 감속 효과: 시작은 빠르게 → 점점 느려짐
                    1 - (1 - it) * (1 - it) * (1 - it)
                }
            )
        )
    }

    // 밀도 정보: 3D 카메라 거리 계산에 사용
    val density = LocalDensity.current.density

    // 전체 다이얼로그 배경 (반투명 검정색)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        // 오른쪽 위 X 버튼 (닫기용)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 24.dp)
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "닫기",
                    tint = Color.Unspecified
                )
            }
        }

        // 중앙 카드
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                // 회전 애니메이션 적용
                .graphicsLayer {
                    this.rotationY = rotationY.value % 360f // 0~359로 나머지 처리
                    cameraDistance = 12 * density
                }
        ) {
            val normalizedRotation = rotationY.value % 360f

            if (normalizedRotation <= 90f || normalizedRotation >= 270f) {
                // 앞면: 0~90도 또는 270~360도
                PlayerCard(name = name, stats = stats, face = CardFace.FRONT)
            } else {
                // 뒷면: 90~270도
                Box(
                    modifier = Modifier.graphicsLayer {
                        this.rotationY = 180f
                    }
                ) {
                    PlayerCard(name = name, stats = stats, face = CardFace.BACK)
                }
            }
        }
    }
}
