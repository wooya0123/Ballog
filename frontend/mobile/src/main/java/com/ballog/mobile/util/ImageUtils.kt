package com.ballog.mobile.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

object ImageUtils {
    /**
     * Uri에서 File로 변환
     */
    fun uriToFile(context: Context, uri: Uri): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val extension = getFileExtension(context, uri)
            val file = File(context.cacheDir, "${UUID.randomUUID()}.$extension")
            
            // 파일 저장
            inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * 이미지 파일을 MultipartBody.Part로 변환
     */
    fun createMultipartFromFile(file: File, paramName: String): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, file.name, requestFile)
    }
    
    /**
     * 파일 확장자 추출
     */
    private fun getFileExtension(context: Context, uri: Uri): String {
        val mime = context.contentResolver.getType(uri)
        return when {
            mime?.contains("jpeg") == true -> "jpg"
            mime?.contains("jpg") == true -> "jpg"
            mime?.contains("png") == true -> "png"
            else -> "jpg"  // 기본값
        }
    }
    
    /**
     * 이미지 리사이징
     */
    fun resizeImage(file: File, maxWidth: Int = 1024, maxHeight: Int = 1024): File? {
        try {
            // 원본 크기 확인
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.path, options)
            
            // 축소 비율 계산
            var scale = 1
            while (options.outWidth / scale > maxWidth || options.outHeight / scale > maxHeight) {
                scale *= 2
            }
            
            // 비트맵 로드
            val bitmap = BitmapFactory.decodeFile(
                file.path,
                BitmapFactory.Options().apply {
                    inSampleSize = scale
                }
            )
            
            // 새 파일에 저장
            val resizedFile = File(file.parentFile, "resized_${file.name}")
            FileOutputStream(resizedFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            
            return resizedFile
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
} 
