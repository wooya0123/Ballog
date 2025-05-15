package com.ballog.mobile.util

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object AudioUtils {
    private const val TAG = "AudioUtils"

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
     * MP4 비디오에서 오디오를 WAV 형식으로 추출합니다.
     */
    fun extractAudioFromVideo(context: Context, videoFile: File): File? {
        Log.d(TAG, "🎬 오디오 추출 시작")
        Log.d(TAG, "📁 입력 비디오 파일: ${videoFile.absolutePath}")
        Log.d(TAG, "📊 비디오 파일 크기: ${videoFile.length() / 1024}KB")

        var extractor = MediaExtractor()
        var outputStream: FileOutputStream? = null

        try {
            // WAV 파일 생성
            val fileName = "extracted_audio_${System.currentTimeMillis()}.wav"
            val audioFile = File(context.getExternalFilesDir(null), fileName)
            outputStream = FileOutputStream(audioFile)
            Log.d(TAG, "📁 WAV 파일 생성: ${audioFile.absolutePath}")

            // 비디오 파일에서 MediaExtractor 설정
            Log.d(TAG, "⚙️ MediaExtractor 설정 시작")
            extractor.setDataSource(videoFile.absolutePath)
            Log.d(TAG, "✅ MediaExtractor 데이터 소스 설정 완료")

            // 오디오 트랙 찾기
            Log.d(TAG, "🔍 오디오 트랙 검색 시작")
            Log.d(TAG, "📊 전체 트랙 수: ${extractor.trackCount}")

            var audioTrackIndex = -1
            var format: MediaFormat? = null

            for (i in 0 until extractor.trackCount) {
                val trackFormat = extractor.getTrackFormat(i)
                val mime = trackFormat.getString(MediaFormat.KEY_MIME)
                Log.d(TAG, "🎵 트랙 #$i MIME 타입: $mime")

                if (mime?.startsWith("audio/") == true) {
                    audioTrackIndex = i
                    format = trackFormat
                    Log.d(TAG, "✅ 오디오 트랙 발견: 트랙 #$i")
                    // 오디오 포맷 정보 출력
                    trackFormat.let { audioFormat ->
                        Log.d(TAG, "📊 오디오 포맷 정보:")
                        Log.d(TAG, "- 채널 수: ${audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)}")
                        Log.d(TAG, "- 샘플레이트: ${audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)}Hz")
                        if (audioFormat.containsKey(MediaFormat.KEY_BIT_RATE)) {
                            Log.d(TAG, "- 비트레이트: ${audioFormat.getInteger(MediaFormat.KEY_BIT_RATE)}bps")
                        }
                        if (audioFormat.containsKey(MediaFormat.KEY_DURATION)) {
                            Log.d(TAG, "- 재생 시간: ${audioFormat.getLong(MediaFormat.KEY_DURATION) / 1000000}초")
                        }
                    }
                    break
                }
            }

            if (audioTrackIndex == -1 || format == null) {
                Log.e(TAG, "❌ 오디오 트랙을 찾을 수 없음")
                return null
            }

            // 오디오 트랙 선택
            Log.d(TAG, "⚙️ 오디오 트랙 선택: 트랙 #$audioTrackIndex")
            extractor.selectTrack(audioTrackIndex)

            // WAV 헤더에 필요한 정보 추출
            val channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            val bitsPerSample = 16  // PCM 16비트로 고정

            // 버퍼 설정
            val bufferSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            Log.d(TAG, "📊 버퍼 크기: ${bufferSize}bytes")
            val buffer = ByteBuffer.allocate(bufferSize)

            // 먼저 데이터 크기를 계산
            var totalAudioLen = 0L
            while (true) {
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break
                totalAudioLen += sampleSize
                extractor.advance()
            }

            // WAV 헤더 쓰기
            val wavHeader = createWavHeader(totalAudioLen, sampleRate, channels, bitsPerSample)
            outputStream.write(wavHeader)

            // 오디오 데이터 쓰기를 위해 Extractor 리셋
            extractor.release()
            extractor = MediaExtractor()
            extractor.setDataSource(videoFile.absolutePath)
            extractor.selectTrack(audioTrackIndex)

            // 오디오 데이터 추출 및 파일로 쓰기
            Log.d(TAG, "📥 오디오 데이터 추출 시작")
            var sampleCount = 0
            var writtenBytes = 0L

            while (true) {
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) {
                    Log.d(TAG, "✅ 모든 샘플 추출 완료")
                    break
                }

                buffer.limit(sampleSize)
                outputStream.write(buffer.array(), 0, sampleSize)
                writtenBytes += sampleSize
                extractor.advance()

                sampleCount++
                if (sampleCount % 100 == 0) {
                    Log.d(TAG, "📊 추출 진행 상황 - 샘플 수: $sampleCount, 총 데이터: ${writtenBytes / 1024}KB")
                }
            }

            Log.d(TAG, "📊 최종 추출 결과:")
            Log.d(TAG, "- 총 샘플 수: $sampleCount")
            Log.d(TAG, "- 총 데이터 크기: ${writtenBytes / 1024}KB")
            Log.d(TAG, "- 출력 파일 크기: ${audioFile.length() / 1024}KB")

            return audioFile
        } catch (e: Exception) {
            Log.e(TAG, "❌ 오디오 추출 중 오류 발생", e)
            Log.e(TAG, "⚠️ 오류 메시지: ${e.message}")
            Log.e(TAG, "⚠️ 오류 종류: ${e.javaClass.simpleName}")
            return null
        } finally {
            try {
                Log.d(TAG, "🔄 리소스 정리 시작")
                outputStream?.close()
                extractor.release()
                Log.d(TAG, "✅ 리소스 정리 완료")
            } catch (e: Exception) {
                Log.e(TAG, "⚠️ 리소스 정리 중 오류 발생", e)
            }
        }
    }
} 
