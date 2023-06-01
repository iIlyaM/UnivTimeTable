package vsu.cs.univtimetable.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import vsu.cs.univtimetable.dto.CreateUnivDto
import vsu.cs.univtimetable.dto.UnivDto
import vsu.cs.univtimetable.dto.UnivResponseDto

interface UnivApi {

    @Headers("Content-Type: application/json")
    @GET("universities")
    fun getUniversities(
        @Header("Authorization") basicToken: String,
        @Query("universityName") univName: String?,
        @Query("order") order: String?
    ): Call<UnivResponseDto>

    @Headers("Content-Type: application/json")
    @PUT("universities/{id}")
    fun editUniversity(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int,
        @Body university: UnivDto
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("universities/create")
    fun addUniversity(
        @Header("Authorization") basicToken: String,
        @Body university: CreateUnivDto
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @DELETE("universities/{id}")
    fun deleteUniversity(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int
    ): Call<Void>
}