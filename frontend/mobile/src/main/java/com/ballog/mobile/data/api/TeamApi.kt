package com.ballog.mobile.data.api

import com.ballog.mobile.data.dto.TeamListResponse
import com.ballog.mobile.data.dto.TeamAddRequest
import com.ballog.mobile.data.dto.TeamDetailResponse
import com.ballog.mobile.data.dto.TeamMemberAddRequest
import com.ballog.mobile.data.dto.TeamMemberListResponse
import com.ballog.mobile.data.dto.TeamInviteResponse
import com.ballog.mobile.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TeamApi {

    @GET("v1/teams")
    suspend fun getUserTeamList(
        @Header("Authorization") token: String
    ): Response<ApiResponse<TeamListResponse>>

    @POST("v1/teams")
    suspend fun addTeam(
        @Header("Authorization") token: String,
        @Body request: TeamAddRequest
    ): Response<ApiResponse<Unit>>

    @POST("v1/teams/invitation")
    suspend fun addTeamMember(
        @Header("Authorization") token: String,
        @Body request: TeamMemberAddRequest
    ): Response<ApiResponse<Unit>>

    @GET("v1/teams/{teamId}")
    suspend fun getTeamDetail(
        @Header("Authorization") token: String,
        @Path("teamId") teamId: Int
    ): Response<ApiResponse<TeamDetailResponse>>

    @GET("v1/teams/{teamId}/members")
    suspend fun getTeamMemberList(
        @Header("Authorization") token: String,
        @Path("teamId") teamId: Int
    ): Response<ApiResponse<TeamMemberListResponse>>
}
