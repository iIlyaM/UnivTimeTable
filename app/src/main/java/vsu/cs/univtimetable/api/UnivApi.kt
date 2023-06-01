package vsu.cs.univtimetable.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path
import vsu.cs.univtimetable.dto.UnivDto
import vsu.cs.univtimetable.dto.UnivResponseDto

interface UnivApi {

    @Headers("Content-Type: application/json")
    @GET("universities")
    fun getUniversities(@Header("Authorization") basicToken: String): Call<UnivResponseDto>

    @Headers("Content-Type: application/json")
    @PUT("universities/{id}")
    fun editUniversity(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int,
        @Body university: UnivDto
    ):Call<Void>
}