package com.ballog.mobile.util

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import kotlinx.coroutines.*
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

object AudioUtils {
    private const val TAG = "AudioUtils"
    private const val PROJECT_RESOURCES_PATH = "mobile/src/main/resources/audio"
    private const val GC_FRAME_INTERVAL = 2000
    private const val MEMORY_THRESHOLD = 0.75
    
    // 오디오 품질 설정
    private const val TARGET_SAMPLE_RATE = 22050 // 22.05kHz (CD 품질의 절반)
    private const val TARGET_BIT_DEPTH = 16 // 16비트
    private const val DOWNSAMPLE_FACTOR = 2 // 다운샘플링 비율

    private fun getOptimalBufferSize(): Int {
        return minOf(
            Runtime.getRuntime().maxMemory() / 32, // 사용 가능한 메모리의 1/32
            1024 * 1024 // 최대 1MB
        ).toInt()
    }

    private fun shouldTriggerGC(frameCount: Int): Boolean {
        if (frameCount % GC_FRAME_INTERVAL != 0) return false
        
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        
        return (usedMemory.toDouble() / maxMemory) > MEMORY_THRESHOLD
    }

    /**
     * WAV 파일 헤더를 생성합니다.
     */
    private fun createWavHeader(
        totalAudioLen: Long,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int = 16
    ): ByteArray {
        val totalDataLen = totalAudioLen + 36
        val byteRate = (bitsPerSample * sampleRate * channels) / 8

        return ByteBuffer.allocate(44).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            // RIFF 청크
            put("RIFF".toByteArray())  // ChunkID
            putInt(totalDataLen.toInt())  // ChunkSize
            put("WAVE".toByteArray())  // Format

            // fmt 서브청크
            put("fmt ".toByteArray())  // Subchunk1ID
            putInt(16)  // Subchunk1Size (PCM의 경우 16)
            putShort(1)  // AudioFormat (PCM = 1)
            putShort(channels.toShort())  // NumChannels
            putInt(sampleRate)  // SampleRate
            putInt(byteRate)  // ByteRate
            putShort((channels * bitsPerSample / 8).toShort())  // BlockAlign
            putShort(bitsPerSample.toShort())  // BitsPerSample

            // data 서브청크
            put("data".toByteArray())  // Subchunk2ID
            putInt(totalAudioLen.toInt())  // Subchunk2Size
        }.array()
    }

    /**
     * 파일을 작은 버퍼를 사용하여 복사합니다.
     */
    private fun copyFile(input: FileInputStream, output: FileOutputStream): Long {
        val bufferedInput = BufferedInputStream(input)
        val bufferedOutput = BufferedOutputStream(output)
        val buffer = ByteArray(getOptimalBufferSize())
        var totalBytesRead = 0L
        
        bufferedInput.use { bis ->
            bufferedOutput.use { bos ->
                var bytesRead: Int
                while (bis.read(buffer).also { bytesRead = it } != -1) {
                    bos.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                }
                bos.flush()
            }
        }
        
        return totalBytesRead
    }

    /**
     * 오디오 데이터를 다운샘플링합니다.
     */
    private fun downsampleAudioData(inputBuffer: ByteArray, channels: Int): ByteArray {
        // 16비트 샘플을 가정
        val samplesPerChannel = inputBuffer.size / (2 * channels)
        val outputSize = (samplesPerChannel / DOWNSAMPLE_FACTOR) * 2 * channels
        val outputBuffer = ByteArray(outputSize)
        
        var inputIndex = 0
        var outputIndex = 0
        
        while (outputIndex < outputSize) {
            // 각 채널에 대해 처리
            for (channel in 0 until channels) {
                // 16비트 샘플을 복사
                outputBuffer[outputIndex++] = inputBuffer[inputIndex]
                outputBuffer[outputIndex++] = inputBuffer[inputIndex + 1]
                // DOWNSAMPLE_FACTOR만큼 건너뛰기
                inputIndex += 2 * DOWNSAMPLE_FACTOR
            }
        }
        
        return outputBuffer
    }

    /**
     * MP4 비디오에서 오디오를 WAV 형식으로 추출합니다.
     * @param context Android 컨텍스트
     * @param videoFile 입력 비디오 파일
     * @param saveToProject true인 경우 프로젝트 디렉토리에 저장, false인 경우 앱 디렉토리에 저장
     * @return 생성된 WAV 파일
     */
    suspend fun extractAudioFromVideo(context: Context, videoFile: File, saveToProject: Boolean = false): File? = 
        withContext(Dispatchers.Default) {
            Log.d(TAG, "🎬 오디오 추출 시작")
            Log.d(TAG, "📁 입력 비디오 파일: ${videoFile.absolutePath}")
            Log.d(TAG, "📊 비디오 파일 크기: ${videoFile.length() / 1024}KB")

            if (!videoFile.exists() || !videoFile.canRead()) {
                Log.e(TAG, "❌ 입력 비디오 파일이 존재하지 않거나 읽을 수 없음")
                return@withContext null
            }

            var extractor: MediaExtractor? = null
            var decoder: MediaCodec? = null
            var outputStream: FileOutputStream? = null
            var audioFile: File? = null
            var tempVideoFile: File? = null
            var tempPcmFile: File? = null
            var pcmOutputStream: FileOutputStream? = null

            try {
                // 파일 복사와 디코딩을 병렬로 처리
                val tempVideoDeferred = async {
                    val temp = File(context.getExternalFilesDir(null), "temp_video_${System.currentTimeMillis()}.mp4")
                    FileInputStream(videoFile).use { input ->
                        FileOutputStream(temp).use { output ->
                            copyFile(input, output)
                        }
                    }
                    temp
                }

                // MediaExtractor 설정
                tempVideoFile = tempVideoDeferred.await()
                extractor = MediaExtractor()
                extractor.setDataSource(tempVideoFile.absolutePath)

                val trackCount = extractor.trackCount
                Log.d(TAG, "📊 전체 트랙 수: $trackCount")

                if (trackCount == 0) {
                    Log.e(TAG, "❌ 비디오 파일에 트랙이 없음")
                    return@withContext null
                }

                // 오디오 트랙 찾기
                var audioTrackIndex = -1
                var format: MediaFormat? = null
                var mime: String? = null

                for (i in 0 until trackCount) {
                    try {
                        val trackFormat = extractor.getTrackFormat(i)
                        mime = trackFormat.getString(MediaFormat.KEY_MIME)
                        Log.d(TAG, "🎵 트랙 #$i MIME 타입: $mime")

                        if (mime?.startsWith("audio/") == true) {
                            audioTrackIndex = i
                            format = trackFormat
                            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, TARGET_SAMPLE_RATE)
                            break
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ 트랙 #$i 정보 읽기 실패", e)
                    }
                }

                if (audioTrackIndex == -1 || format == null || mime == null) {
                    Log.e(TAG, "❌ 오디오 트랙을 찾을 수 없음")
                    return@withContext null
                }

                // 임시 PCM 파일 생성
                tempPcmFile = File(context.getExternalFilesDir(null), "temp_pcm_${System.currentTimeMillis()}.pcm")
                pcmOutputStream = FileOutputStream(tempPcmFile)

                // 오디오 디코더 설정
                decoder = MediaCodec.createDecoderByType(mime)
                decoder.configure(format, null, null, 0)
                decoder.start()

                // 오디오 트랙 선택
                extractor.selectTrack(audioTrackIndex)

                // WAV 헤더에 필요한 정보 추출
                val channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                val sampleRate = TARGET_SAMPLE_RATE
                val bitsPerSample = TARGET_BIT_DEPTH

                // 버퍼 설정
                val inputBufferSize = minOf(format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE), getOptimalBufferSize())
                val inputBuffer = ByteBuffer.allocate(inputBufferSize)
                val bufferInfo = MediaCodec.BufferInfo()

                var isEOS = false
                var frameCount = 0

                // 디코딩 작업 수행
                val decodedBytes = async {
                    var bytesWritten = 0L
                    
                    while (!isEOS) {
                        val inputBufferId = decoder.dequeueInputBuffer(10000)
                        if (inputBufferId >= 0) {
                            val sampleSize = extractor.readSampleData(inputBuffer, 0)
                            val presentationTimeUs = if (sampleSize < 0) -1 else extractor.sampleTime

                            when {
                                sampleSize < 0 -> {
                                    decoder.queueInputBuffer(inputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                                    isEOS = true
                                }
                                else -> {
                                    val codecInputBuffer = decoder.getInputBuffer(inputBufferId)
                                    codecInputBuffer?.clear()
                                    codecInputBuffer?.put(inputBuffer)
                                    decoder.queueInputBuffer(inputBufferId, 0, sampleSize, presentationTimeUs, 0)
                                    extractor.advance()
                                    frameCount++
                                }
                            }
                        }

                        val outputBufferId = decoder.dequeueOutputBuffer(bufferInfo, 10000)
                        if (outputBufferId >= 0) {
                            val outputBuffer = decoder.getOutputBuffer(outputBufferId)
                            if (outputBuffer != null && bufferInfo.size > 0) {
                                val chunk = ByteArray(minOf(bufferInfo.size, getOptimalBufferSize()))
                                outputBuffer.get(chunk)
                                
                                val downsampledChunk = downsampleAudioData(chunk, channels)
                                pcmOutputStream?.write(downsampledChunk)
                                bytesWritten += downsampledChunk.size
                            }
                            decoder.releaseOutputBuffer(outputBufferId, false)

                            if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                break
                            }
                        }

                        if (shouldTriggerGC(frameCount)) {
                            System.gc()
                        }
                    }
                    
                    bytesWritten
                }

                val totalBytesWritten = decodedBytes.await()
                pcmOutputStream?.close()

                if (totalBytesWritten > 0) {
                    // WAV 파일 생성
                    audioFile = if (saveToProject) {
                        val resourcesDir = File(PROJECT_RESOURCES_PATH)
                        if (!resourcesDir.exists()) {
                            resourcesDir.mkdirs()
                        }
                        File(resourcesDir, "extracted_audio_${System.currentTimeMillis()}.wav")
                    } else {
                        File(context.getExternalFilesDir(null), "extracted_audio_${System.currentTimeMillis()}.wav")
                    }

                    outputStream = FileOutputStream(audioFile)
                    
                    // WAV 헤더 쓰기
                    val wavHeader = createWavHeader(totalBytesWritten, sampleRate, channels, bitsPerSample)
                    outputStream.write(wavHeader)

                    // PCM 데이터 복사
                    FileInputStream(tempPcmFile).use { input ->
                        copyFile(input, outputStream)
                    }

                    Log.d(TAG, "✅ WAV 파일 생성 완료")
                    Log.d(TAG, "📁 WAV 파일 저장 위치: ${audioFile.absolutePath}")
                    Log.d(TAG, "📊 최종 파일 크기: ${audioFile.length() / 1024}KB")
                } else {
                    Log.e(TAG, "❌ 추출된 오디오 데이터가 없음")
                    return@withContext null
                }

                audioFile

            } catch (e: Exception) {
                Log.e(TAG, "❌ 오디오 추출 중 오류 발생", e)
                audioFile?.delete()
                null
            } finally {
                try {
                    outputStream?.close()
                    pcmOutputStream?.close()
                    decoder?.stop()
                    decoder?.release()
                    extractor?.release()
                    tempVideoFile?.delete()
                    tempPcmFile?.delete()
                } catch (e: Exception) {
                    Log.e(TAG, "⚠️ 리소스 정리 중 오류 발생", e)
                }
            }
        }
} 
