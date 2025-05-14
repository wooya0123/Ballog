package notfound.ballog.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.ballog.watch.ui.components.BallogButton
import com.ballog.watch.ui.theme.BallogCyan

@Composable
fun InstructionScreen(onContinueClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "경기장을 한바퀴 돌면서 각 모서리에서 측정 버튼을 눌러주세요",
            textAlign = TextAlign.Center,
            color = BallogCyan,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        BallogButton(
            text = "시작하기",
            onClick = onContinueClick,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
