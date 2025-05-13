package com.ballog.mobile

import android.app.Application
import android.content.Intent
import android.util.Log
import com.ballog.mobile.data.api.RetrofitInstance
import com.ballog.mobile.data.local.TokenManager
import com.ballog.mobile.data.service.WearableDataService
import com.ballog.mobile.util.S3Utils

class BallogApplication : Application() {
    private val TAG = "BallogApplication"
    
    lateinit var tokenManager: TokenManager
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Retrofit 초기화 (TokenManager 포함)
        RetrofitInstance.init(this)
        tokenManager = RetrofitInstance.getTokenManager()
        
        // S3 유틸리티 초기화
        S3Utils.init(this)
        
        // WearableDataService는 앱이 포그라운드에 있을 때만 시작
        try {
            val serviceIntent = Intent(this, WearableDataService::class.java)
            startForegroundService(serviceIntent)
            Log.d(TAG, "WearableDataService 시작 요청")
        } catch (e: Exception) {
            Log.e(TAG, "WearableDataService 시작 실패: ${e.message}")
        }
    }

    companion object {
        private lateinit var instance: BallogApplication
        
        fun getInstance(): BallogApplication {
            return instance
        }
    }

    init {
        instance = this
    }
} 