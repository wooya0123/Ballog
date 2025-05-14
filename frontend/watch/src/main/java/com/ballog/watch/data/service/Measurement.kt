package com.ballog.watch.data.service

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.gms.location.LocationServices
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.delay
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.ballog.watch.ui.components.BallogButton
import com.ballog.watch.ui.theme.BallogCyan
import com.ballog.watch.ui.theme.BallogWhite

fun saveLocationsToFile(context: Context, locations: List<Location>) {
    try {
        val file = File(context.filesDir, "field_corners.txt")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = dateFormat.format(Date())

        val content = buildString {
            appendLine("=== 경기장 모서리 좌표 ===")
            appendLine("저장 시간: $currentTime")
            appendLine()
            locations.forEachIndexed { index, location ->
                appendLine("모서리 ${index + 1}:")
                appendLine("위도: ${location.latitude}")
                appendLine("경도: ${location.longitude}")
                appendLine()
            }
        }

        file.writeText(content)
        Log.d("FieldCorners", "좌표가 파일에 저장되었습니다: ${file.absolutePath}")
    } catch (e: Exception) {
        Log.e("FieldCorners", "파일 저장 실패: ${e.message}")
    }
}

@Composable
fun MeasurementScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val dataClient = remember { Wearable.getDataClient(context) }

    // 상태 관리
    var measurementCount by remember { mutableStateOf(0) }
    var isCountingDown by remember { mutableStateOf(false) }
    var countdownValue by remember { mutableStateOf(3) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isMeasuring by remember { mutableStateOf(false) }  // 측정 중 상태 추가
    val corners = listOf("첫 번째", "두 번째", "세 번째", "네 번째")
    val locationsList = remember { mutableStateListOf<Location>() }

    // 완료 화면 상태
    var showCompletionScreen by remember { mutableStateOf(false) }
    var isDataSending by remember { mutableStateOf(false) }
    var isDataSent by remember { mutableStateOf(false) }

    // 위치 권한 상태
    var hasLocationPermission by remember { mutableStateOf(false) }

    // 위치 권한 요청
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (!isGranted) {
            errorMessage = "위치 권한이 필요합니다"
        }
    }

    // 앱 시작시 위치 권한 요청
    LaunchedEffect(key1 = Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // GPS 활성화 확인
    fun isGpsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 측정 위치 저장 함수
    fun saveLocation(location: Location) {
        locationsList.add(location)
        measurementCount++
        errorMessage = null

        // 4개 모두 측정 완료시 완료 화면 표시 및 파일 저장
        if (measurementCount == 4) {
            showCompletionScreen = true
            saveLocationsToFile(context, locationsList)
        }
    }

    // 삼성 헬스 달리기 기능 실행 함수
    fun launchSamsungHealthRunning(context: Context) {
        try {
            val intent = Intent().apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
                setPackage("com.samsung.android.wear.shealth")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
            Toast.makeText(context, "볼로그를 선택해주세요", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("SamsungHealth", "실행 실패: ${e.message}")
            Toast.makeText(context, "삼성 헬스를 실행할 수 없습니다", Toast.LENGTH_LONG).show()
        }
    }

    // 데이터 전송 함수
    fun sendDataToPhone() {
        isDataSending = true

        // 데이터가 없으면 진행하지 않음
        if (locationsList.size < 4) {
            errorMessage = "모든 모서리를 측정해야 합니다"
            isDataSending = false
            return
        }

        scope.launch {
            try {
                sendLocationsToPhone(dataClient, locationsList)
                isDataSending = false
                isDataSent = true

                // 5초 후에 운동 앱 실행
                delay(5000)
                launchSamsungHealthRunning(context)
                onComplete()
            } catch (e: Exception) {
                errorMessage = "데이터 전송 실패: ${e.message}"
                isDataSending = false
            }
        }
    }

    // 위치 측정 시도 함수
    fun attemptLocationMeasurement() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

            isMeasuring = true  // 측정 시작
            errorMessage = null

            val locationRequest = LocationRequest.Builder(0)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(0)
                .setMaxUpdateDelayMillis(0)
                .setMinUpdateDistanceMeters(0f)
                .build()

            fusedLocationClient.requestLocationUpdates(locationRequest,
                object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                        val location = locationResult.lastLocation
                        if (location != null) {
                            fusedLocationClient.removeLocationUpdates(this)
                            saveLocation(location)
                            isMeasuring = false  // 측정 완료
                        } else {
                            errorMessage = "위치를 가져올 수 없습니다. 다시 시도해주세요."
                            fusedLocationClient.removeLocationUpdates(this)
                            isMeasuring = false  // 측정 실패
                        }
                    }
                },
                null
            )
        } else {
            errorMessage = "위치 권한이 필요합니다"
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // 측정 버튼 클릭 처리
    fun onMeasureClick() {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        if (!isGpsEnabled()) {
            errorMessage = "GPS를 활성화해주세요"
            return
        }

        isCountingDown = true
        errorMessage = null
    }

    // 측정 화면 UI 부분 수정
    if (isCountingDown) {
        // 카운트다운 UI
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = countdownValue.toString(),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = BallogCyan
            )
        }

        LaunchedEffect(isCountingDown) {
            for (i in 3 downTo 1) {
                countdownValue = i
                delay(1000)
            }

            // 카운트다운 종료 후 위치 측정
            isCountingDown = false
            attemptLocationMeasurement()
        }
    } else if (isMeasuring) {
        // 측정 중 UI
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "위치 측정 중...",
                    color = BallogWhite,
                    fontSize = 14.sp
                )
            }
        }
    } else if (showCompletionScreen) {
        // 측정 완료 화면
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 완료 메시지 또는 전송 완료 메시지
            item {
                Text(
                    text = if (isDataSent) "데이터 전송 완료!" else "측정 완료!",
                    color = BallogWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 안내 메시지
            item {
                if (isDataSent) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "5초 후 삼성 헬스로 이동합니다",
                            color = BallogWhite,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "삼성 헬스에서 볼로그를\n선택해주세요",
                            color = BallogCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                    }
                } else {
                    Text(
                        text = "신나게 뛰세요\n볼로그가 측정해드릴게요!",
                        color = BallogCyan,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // 데이터 전송 버튼 또는 진행 상태
            if (!isDataSent) {
                item {
                    if (isDataSending) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "데이터 전송 중...",
                                color = BallogWhite,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        BallogButton(
                            text = "데이터 전송",
                            onClick = { sendDataToPhone() },
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }

            // 오류 메시지 표시
            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colors.error,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }
        }
    } else {
        // 스크롤 가능한 측정 화면
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (measurementCount < 4) {
                // 모서리 번호 표시
                item {
                    Text(
                        text = "${corners[measurementCount]} 모서리",
                        color = BallogCyan,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // 측정 버튼
                item {
                    BallogButton(
                        text = "측정",
                        onClick = { onMeasureClick() },
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // 측정 상태 표시
                item {
                    Text(
                        text = "$measurementCount/4 측정 완료",
                        fontSize = 14.sp,
                        color = BallogWhite,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                // 오류 메시지 표시
                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colors.error,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

// 핸드폰으로 위치 데이터 전송
suspend fun sendLocationsToPhone(dataClient: DataClient, locations: List<Location>) {
    val request = PutDataMapRequest.create("/field_corners").apply {
        dataMap.putDouble("lat1", locations[0].latitude)
        dataMap.putDouble("lon1", locations[0].longitude)
        dataMap.putDouble("lat2", locations[1].latitude)
        dataMap.putDouble("lon2", locations[1].longitude)
        dataMap.putDouble("lat3", locations[2].latitude)
        dataMap.putDouble("lon3", locations[2].longitude)
        dataMap.putDouble("lat4", locations[3].latitude)
        dataMap.putDouble("lon4", locations[3].longitude)
        dataMap.putLong("timestamp", System.currentTimeMillis())
    }

    try {
        dataClient.putDataItem(request.asPutDataRequest().setUrgent()).await()
        Log.d("WatchDataTransfer", "데이터 전송 성공: ${request.dataMap}")
    } catch (e: Exception) {
        Log.e("WatchDataTransfer", "데이터 전송 실패: ${e.message}")
        throw e
    }
}
