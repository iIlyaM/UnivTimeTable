package vsu.cs.univtimetable.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import vsu.cs.univtimetable.dto.AuthRequestDto
import vsu.cs.univtimetable.dto.AuthResponseDto

interface UserAuthApi {

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    fun login(
        @Body login: AuthRequestDto,
    ): Call<AuthResponseDto>

    @GET("auth/refresh")
    fun refreshToken(
        @Header("Authorization") token: String,
    ): Call<AuthResponseDto>
}