package com.ballog.mobile.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ballog.mobile.R
import com.ballog.mobile.data.repository.MatchRepository
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val TAG = "WearableDataService"
private const val NOTIFICATION_ID = 1
private const val CHANNEL_ID = "WearableDataServiceChannel"

class WearableDataService : Service(), DataClient.OnDataChangedListener {
    private lateinit var matchRepository: MatchRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        matchRepository = MatchRepository(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Wearable Data API 리스너 등록
        Wearable.getDataClient(this).addListener(this)
        Log.d(TAG, "WearableDataService 생성됨")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Wearable Data API 리스너 해제
        Wearable.getDataClient(this).removeListener(this)
        Log.d(TAG, "WearableDataService 소멸됨")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path?.compareTo("/field_corners") == 0) {
                    val dataMapItem = DataMapItem.fromDataItem(dataItem)
                    val dataMap = dataMapItem.dataMap
                    
                    // 경기장 모서리 데이터 처리
                    val corners = dataMap.getDataMapArrayList("corners")
                    if (corners != null) {
                        serviceScope.launch {
                            matchRepository.saveFieldCorners(corners)
                            Log.d(TAG, "경기장 모서리 데이터 저장 완료")
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Wearable Data Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "경기 데이터 동기화 서비스"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("경기 데이터 동기화")
        .setContentText("경기 데이터를 동기화하는 중입니다")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()
}

