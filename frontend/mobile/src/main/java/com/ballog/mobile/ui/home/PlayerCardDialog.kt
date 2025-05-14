package com.ballog.mobile.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ballog.mobile.R
import com.ballog.mobile.ui.theme.Primary
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
    val rotationY by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600), // 600ms 동안 회전
        label = "flip"
    )

    // 다이얼로그가 화면에 나타났을 때 자동으로 flipped → true로 설정해서 회전 시작
    LaunchedEffect(Unit) {
        flipped = true
    }

    // 밀도 정보: 3D 카메라 거리 계산에 사용
    val density = LocalDensity.current.density

    // 전체 다이얼로그 배경 (반투명 검정색)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
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
                    this.rotationY = rotationY         // 현재 회전 상태 반영
                    cameraDistance = 12 * density      // 3D 효과를 위한 거리 설정
                }
                // 카드 뒤에 Glow 효과 그리기
                .drawBehind {
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.35f),  // 중심 밝은색
                                Primary.copy(alpha = 0.0f)    // 바깥 투명
                            ),
                            center = center,
                            radius = size.maxDimension * 0.7f
                        ),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx())
                    )
                }
                // 그림자 효과
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(22.dp)
                )
        ) {
            // 앞면 카드: 회전이 90도 이하일 때 보여짐
            if (rotationY <= 90f) {
                PlayerCard(name = name, stats = stats, face= CardFace.FRONT)

                // 뒷면 카드: 회전이 90도 이상일 때 보여짐
            } else {
                Box(
                    modifier = Modifier.graphicsLayer {
                        this.rotationY = 180f // 반전 처리 (뒤집힌 상태)
                    }
                ) {
                    PlayerCard(name = name, stats = stats, face= CardFace.BACK)
                }
            }
        }
    }
}
