package vsu.cs.univtimetable.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import vsu.cs.univtimetable.dto.auth.AuthRequestDto
import vsu.cs.univtimetable.dto.auth.AuthResponseDto

interface UserAuthApi {

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(
        @Body login: AuthRequestDto,
    ): Response<AuthResponseDto>

    @GET("auth/refresh")
    fun refreshToken(
        @Header("Authorization") token: String,
    ): Call<AuthResponseDto>
}