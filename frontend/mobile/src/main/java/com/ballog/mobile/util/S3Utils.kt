package com.ballog.mobile.util

import android.content.Context
import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.util.UUID

object S3Utils {
    private const val TAG = "S3Utils" // 로그 태그 추가
    
    // S3 구성 상수
    private const val BUCKET_NAME = "ballog-2"  // 버킷 이름 확인
    private const val REGION = "ap-northeast-2"  // 서울 리전
    
    // S3 엔드포인트 형식 (일부 리전에 따라 다를 수 있음)
    // 형식 1: https://버킷명.s3.리전.amazonaws.com/
    // 형식 2: https://s3.리전.amazonaws.com/버킷명/
    private const val URL_FORMAT = 2  // 원래 형식으로 돌려놓음
    
    // 이미지 URL의 기본 경로 (선택한 형식에 따라)
    private val BASE_URL = if (URL_FORMAT == 1) {
        "https://$BUCKET_NAME.s3.$REGION.amazonaws.com/"
    } else {
        "https://s3.$REGION.amazonaws.com/$BUCKET_NAME/"
    }
    
    // AWS 인증 정보
    private var accessKey: String? = null
    private var secretKey: String? = null
    
    /**
     * AWS 자격 증명 초기화
     * assets/aws.properties에서 키 값을 로드
     */
    fun init(context: Context) {
        try {
            Log.d(TAG, "S3 Utils 초기화 시작")
            // assets에서 aws.properties 파일 직접 읽기
            try {
                val fileContents = context.assets.open("aws.properties").bufferedReader().use { it.readText() }
                Log.d(TAG, "aws.properties 파일 읽기 성공 (${fileContents.length} 바이트)")
                // 파일 내용에서 키 값을 추출
                fileContents.lines().forEach { line ->
                    when {
                        line.startsWith("aws.access_key=") -> {
                            accessKey = line.substringAfter("aws.access_key=").trim()
                            Log.d(TAG, "액세스 키 추출: $accessKey")
                        }
                        line.startsWith("aws.secret_key=") -> {
                            secretKey = line.substringAfter("aws.secret_key=").trim()
                            Log.d(TAG, "시크릿 키 추출 (첫 5자): ${secretKey?.take(5)}")
                        }
                    }
                }
                Log.d(TAG, "AWS 자격 증명 로드 완료")
            } catch (e: Exception) {
                Log.e(TAG, "aws.properties 파일 읽기 실패: ${e.message}")
                e.printStackTrace()
            }
            // 키가 설정되어 있지 않으면 로그로 경고
            if (accessKey.isNullOrEmpty() || secretKey.isNullOrEmpty()) {
                Log.e(TAG, "WARNING: AWS credentials not properly configured!")
            }
        } catch (e: Exception) {
            Log.e(TAG, "S3Utils 초기화 실패: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * S3에 이미지 파일을 업로드하고 해당 URL을 반환
     * 
     * @param file 업로드할 이미지 파일
     * @param folderName S3 내의 폴더 이름 (e.g. 'profile', 'team-logo' 등)
     * @return 업로드된 이미지의 URL (서명된 URL)
     */
    suspend fun uploadImageToS3(file: File, folderName: String = "profile"): String = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "S3 이미지 업로드 시작: ${file.name} (${file.length()}바이트)")
            
            // 자격 증명이 설정되어 있는지 확인
            if (accessKey.isNullOrEmpty() || secretKey.isNullOrEmpty()) {
                Log.e(TAG, "AWS 자격 증명이 설정되지 않았습니다!")
                throw IllegalStateException("AWS 자격 증명이 설정되지 않았습니다. S3Utils.init()을 먼저 호출하세요.")
            }
            
            // 마지막 안전 장치: 따옴표 제거 확인
            val finalAccessKey = accessKey?.replace("\"", "")?.replace("'", "")?.trim() ?: ""
            val finalSecretKey = secretKey?.replace("\"", "")?.replace("'", "")?.trim() ?: ""
            
            if (finalAccessKey.isEmpty() || finalSecretKey.isEmpty()) {
                Log.e(TAG, "최종 처리 후 AWS 자격 증명이 비어 있습니다!")
                throw IllegalStateException("AWS 자격 증명이 비어 있습니다.")
            }
            
            Log.d(TAG, "AWS 자격 증명 처리 완료, S3 클라이언트 생성")
            
            // S3 클라이언트 생성
            val credentials = BasicAWSCredentials(finalAccessKey, finalSecretKey)
            val s3Client = AmazonS3Client(credentials)
            
            // 리전 설정
            val regionName = REGION.replace("-", "_").uppercase()
            Log.d(TAG, "설정할 리전: $regionName")
            
            try {
                val region = Region.getRegion(Regions.valueOf(regionName))
                s3Client.setRegion(region)
                Log.d(TAG, "S3 클라이언트 리전 설정 완료: ${region.getName()}")
            } catch (e: Exception) {
                Log.e(TAG, "리전 설정 오류: ${e.message}")
                // 기본값으로 설정 시도
                s3Client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2))
                Log.d(TAG, "기본 리전(AP_NORTHEAST_2)으로 설정")
            }
            
            // 파일명 생성 (중복 방지를 위해 UUID 사용)
            val fileName = "${folderName}/${UUID.randomUUID()}_${file.name}"
            Log.d(TAG, "업로드할 파일 경로: $fileName")
            
            // 파일 전체 내용을 바이트 배열로 읽어오기
            val fileBytes = file.readBytes()
            Log.d(TAG, "파일을 바이트 배열로 읽음 (${fileBytes.size} 바이트)")
            
            // 바이트 배열로부터 스트림 생성
            val byteArrayInputStream = ByteArrayInputStream(fileBytes)
            
            // 파일 메타데이터 설정
            val metadata = ObjectMetadata().apply {
                contentType = "image/jpeg"
                contentLength = fileBytes.size.toLong()
            }
            
            // 파일 업로드 요청 생성 - ACL 제거 (The bucket does not allow ACLs 오류 수정)
            val request = PutObjectRequest(
                BUCKET_NAME,
                fileName,
                byteArrayInputStream,
                metadata
            )
            
            Log.d(TAG, "업로드 요청 생성 완료, 업로드 시작...")
            
            try {
                // 업로드 실행
                Log.d(TAG, "putObject 호출 직전")
                s3Client.putObject(request)
                Log.d(TAG, "putObject 호출 성공")
                
                // 간단한 URL 생성 (서명 없음, 버킷이 공개 접근 가능해야 함)
                val simpleUrl = "$BASE_URL$fileName"
                Log.d(TAG, "업로드 성공, 간단한 URL: $simpleUrl")
                
                // 간단한 URL 반환 (서명 정보 없음)
                return@withContext simpleUrl
            } finally {
                // 스트림 닫기
                byteArrayInputStream.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "S3 업로드 오류: ${e.message}")
            Log.e(TAG, "오류 세부 정보: ${e.javaClass.simpleName}")
            e.printStackTrace()
            throw e
        }
    }

}
