package vsu.cs.univtimetable.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import vsu.cs.univtimetable.dto.UnivResponseDto

interface UnivApi {

    @Headers("Content-Type: application/json")
    @GET("universities")
    fun getUniversities(): Call<UnivResponseDto>
}