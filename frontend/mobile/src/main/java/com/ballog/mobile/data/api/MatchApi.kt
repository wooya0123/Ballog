package com.ballog.mobile.data.api

import com.ballog.mobile.data.dto.MatchListResponse
import com.ballog.mobile.data.dto.MatchRegisterRequest
import com.ballog.mobile.data.dto.StadiumListResult
import com.ballog.mobile.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MatchApi {
    // 내 매치 리스트 조회
    @GET("v1/matches/me")
    suspend fun getMyMatches(
        @Header("Authorization") token: String,
        @Query("month") month: String
    ): Response<ApiResponse<MatchListResponse>>

    // 특정 팀의 매치 리스트 조회
    @GET("v1/matches/teams/{teamId}")
    suspend fun getTeamMatches(
        @Header("Authorization") token: String,
        @Path("teamId") teamId: Long,
        @Query("month") month: String
    ): Response<ApiResponse<MatchListResponse>>

    // 경기장 조회
    @GET("v1/matches/stadiums")
    suspend fun getStadiumList(
        @Header("Authorization") token: String
    ): Response<ApiResponse<StadiumListResult>>

    // 매치 등록
    @POST("v1/matches/me")
    suspend fun registerMyMatch(
        @Header("Authorization") token: String,
        @Body request: MatchRegisterRequest
    ): Response<ApiResponse<Unit>>

}
