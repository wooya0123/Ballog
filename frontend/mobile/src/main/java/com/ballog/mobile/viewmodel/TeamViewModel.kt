package com.ballog.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ballog.mobile.BallogApplication
import com.ballog.mobile.data.api.RetrofitInstance
import com.ballog.mobile.data.dto.*
import com.ballog.mobile.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class TeamViewModel : ViewModel() {
    private val tokenManager = BallogApplication.getInstance().tokenManager
    private val teamApi = RetrofitInstance.teamApi

    // 상태 정의
    private val _teamList = MutableStateFlow<List<Team>>(emptyList())
    val teamList: StateFlow<List<Team>> = _teamList.asStateFlow()

    private val _teamDetail = MutableStateFlow<TeamDetail?>(null)
    val teamDetail: StateFlow<TeamDetail?> = _teamDetail.asStateFlow()

    private val _teamMembers = MutableStateFlow<List<TeamMemberModel>>(emptyList())
    val teamMembers: StateFlow<List<TeamMemberModel>> = _teamMembers.asStateFlow()

    private val _inviteLink = MutableStateFlow<String?>(null)
    val inviteLink: StateFlow<String?> = _inviteLink.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun setError(message: String?) {
        _error.value = message
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun setInviteLink(link: String?) {
        _inviteLink.value = link
    }

    // 팀 목록 조회
    fun getUserTeamList() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = tokenManager.getAccessToken().first()
                if (token == null) {
                    _error.value = "로그인이 필요합니다"
                    return@launch
                }

                val response = teamApi.getUserTeamList("Bearer $token")
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true && apiResponse.result != null) {
                        _teamList.value = apiResponse.result.teamList.map { it.toTeam() }
                    } else {
                        _error.value = apiResponse?.message ?: "팀 목록 조회에 실패했습니다"
                    }
                } else {
                    _error.value = "서버 오류가 발생했습니다"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 팀 생성
    suspend fun addTeam(teamName: String, logoImageUrl: String, foundationDate: String): Result<Unit> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            val token = tokenManager.getAccessToken().first()
            if (token == null) {
                return Result.failure(Exception("로그인이 필요합니다"))
            }

            val request = TeamAddRequest(teamName, logoImageUrl, foundationDate)
            val response = teamApi.addTeam("Bearer $token", request)
            
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                getUserTeamList() // 팀 목록 갱신
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "팀 생성에 실패했습니다"
                _error.value = errorMessage
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            _error.value = e.message
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    // 초대 링크 생성
    suspend fun generateInviteLink(teamId: Int): Result<String> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            println("TeamViewModel: 초대 링크 생성 시작, teamId: $teamId")
            
            if (teamId <= 0) {
                println("TeamViewModel: 유효하지 않은 teamId: $teamId")
                _error.value = "유효하지 않은 팀 ID입니다"
                return Result.failure(Exception("유효하지 않은 팀 ID입니다"))
            }
            
            // 임의의 초대 코드 생성 (UUID의 마지막 12자리 사용)
            val randomCode = UUID.randomUUID().toString().takeLast(12)
            println("TeamViewModel: 생성된 초대 코드: $randomCode")
            
            // 커스텀 URL 스킴을 사용한 딥 링크 생성
            // 형식이 모호할 수 있으므로 명확한 형식으로 수정
            val inviteLink = "ballog://team-invite?teamId=$teamId&code=$randomCode"
            println("TeamViewModel: 생성된 초대 링크: $inviteLink")
            
            // 실제 적용 시 복사용 링크 형태로도 출력
            val shareableLink = "ballog://team-invite?teamId=$teamId&code=$randomCode"
            println("TeamViewModel: 공유 가능한 링크: $shareableLink")
            
            _inviteLink.value = inviteLink
            Result.success(inviteLink)
        } catch (e: Exception) {
            println("TeamViewModel: 예외 발생: ${e.message}")
            e.printStackTrace()
            _error.value = e.message
            Result.failure(e)
        } finally {
            _isLoading.value = false
            println("TeamViewModel: 초대 링크 생성 종료")
        }
    }

    // 팀원 추가
    suspend fun addTeamMember(teamId: Int, role: String = "MEMBER"): Result<Unit> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            val token = tokenManager.getAccessToken().first()
            if (token == null) {
                return Result.failure(Exception("로그인이 필요합니다"))
            }

            val request = TeamMemberAddRequest(teamId, role)
            val response = teamApi.addTeamMember("Bearer $token", request)
            
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                getTeamMemberList(teamId) // 멤버 목록 갱신
                getUserTeamList() // 팀 목록 갱신
                Result.success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "팀원 추가에 실패했습니다"
                _error.value = errorMessage
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            _error.value = e.message
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    // 팀 상세 정보 조회
    fun getTeamDetail(teamId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                println("TeamViewModel: getTeamDetail 시작, teamId: $teamId")
                
                if (teamId <= 0) {
                    _error.value = "유효하지 않은 팀 ID입니다"
                    _isLoading.value = false
                    println("TeamViewModel: 유효하지 않은 teamId: $teamId")
                    return@launch
                }
                
                val token = tokenManager.getAccessToken().first()
                if (token == null) {
                    _error.value = "로그인이 필요합니다"
                    _isLoading.value = false
                    println("TeamViewModel: 토큰이 null입니다")
                    return@launch
                }
                println("TeamViewModel: 토큰 획득 성공")

                try {
                    println("TeamViewModel: API 호출 시작")
                    val response = teamApi.getTeamDetail("Bearer $token", teamId)
                    println("TeamViewModel: API 응답 코드: ${response.code()}")
                    
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        println("TeamViewModel: 응답 본문: $apiResponse")
                        println("TeamViewModel: isSuccess: ${apiResponse?.isSuccess}, 메시지: ${apiResponse?.message}")
                        
                        if (apiResponse?.isSuccess == true && apiResponse.result != null) {
                            try {
                                println("TeamViewModel: 결과 데이터: ${apiResponse.result}")
                                
                                // API 응답에서 각 필드의 null 여부 확인
                                val responseData = apiResponse.result
                                println("TeamViewModel: 팀 이름: ${responseData.teamName}, 로고URL: ${responseData.logoImageUrl}")
                                println("TeamViewModel: averageCardStats 존재: ${responseData.averageCardStats != null}")
                                println("TeamViewModel: playerCards 존재: ${responseData.playerCards != null}, 크기: ${responseData.playerCards?.size ?: 0}")
                                
                                if (responseData.averageCardStats == null) {
                                    println("TeamViewModel: averageCardStats가 null입니다.")
                                }
                                
                                if (responseData.playerCards == null || responseData.playerCards.isEmpty()) {
                                    println("TeamViewModel: playerCards가 null이거나 비어 있습니다.")
                                } else {
                                    // 첫 번째 플레이어 카드 데이터 로깅
                                    val firstPlayer = responseData.playerCards.firstOrNull()
                                    println("TeamViewModel: 첫번째 플레이어 - 닉네임: ${firstPlayer?.nickName}, 카드통계: ${firstPlayer?.cardStats != null}")
                                }
                                
                                val teamDetail = apiResponse.result.toTeamDetail()
                                println("TeamViewModel: 변환 성공, 팀명: ${teamDetail.name}, 멤버 수: ${teamDetail.players.size}")
                                _teamDetail.value = teamDetail
                            } catch (e: Exception) {
                                println("TeamViewModel: 데이터 변환 실패: ${e.message}")
                                e.printStackTrace()
                                _error.value = "데이터 형식이 올바르지 않습니다: ${e.message}"
                            }
                        } else {
                            println("TeamViewModel: API 응답 실패: ${apiResponse?.message}")
                            _error.value = apiResponse?.message ?: "팀 상세 정보 조회에 실패했습니다"
                        }
                    } else {
                        println("TeamViewModel: HTTP 오류: ${response.code()}, 메시지: ${response.message()}")
                        _error.value = "서버 오류가 발생했습니다 (${response.code()})"
                    }
                } catch (e: Exception) {
                    println("TeamViewModel: 서버 연결 오류: ${e.message}")
                    e.printStackTrace()
                    _error.value = "서버 연결에 실패했습니다: ${e.message}"
                }
            } catch (e: Exception) {
                println("TeamViewModel: 알 수 없는 오류: ${e.message}")
                e.printStackTrace()
                _error.value = "알 수 없는 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
                println("TeamViewModel: getTeamDetail 완료")
            }
        }
    }

    // 팀 멤버 목록 조회
    fun getTeamMemberList(teamId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = tokenManager.getAccessToken().first()
                if (token == null) {
                    _error.value = "로그인이 필요합니다"
                    return@launch
                }

                val response = teamApi.getTeamMemberList("Bearer $token", teamId)
                println("TeamViewModel: 팀 멤버 목록 응답 - $response")
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    println("TeamViewModel: 응답 본문 - $apiResponse")
                    println("TeamViewModel: isSuccess - ${apiResponse?.isSuccess}, 메시지 - ${apiResponse?.message}")
                    println("TeamViewModel: 결과 데이터 - ${apiResponse?.result}")
                    
                    if (apiResponse?.isSuccess == true && apiResponse.result != null) {
                        println("TeamViewModel: 팀 멤버 목록 - ${apiResponse.result.teamMemberList}")
                        _teamMembers.value = apiResponse.result.teamMemberList.map { it.toTeamMemberModel() }
                    } else {
                        _error.value = apiResponse?.message ?: "팀 멤버 목록 조회에 실패했습니다"
                    }
                } else {
                    _error.value = "서버 오류가 발생했습니다"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
