package vsu.cs.univtimetable.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import vsu.cs.univtimetable.dto.univ.CreateUnivDto
import vsu.cs.univtimetable.dto.univ.UnivDto
import vsu.cs.univtimetable.dto.univ.UnivResponseDto

interface UnivApi {

    @Headers("Content-Type: application/json")
    @GET("universities/v2")
    suspend fun getUniversities(
        @Header("Authorization") basicToken: String,
        @Query("universityName") univName: String?,
        @Query("order") order: String?
    ): Response<List<UnivDto>>

    @Headers("Content-Type: application/json")
    @GET("universities/{id}")
    suspend fun getUniversity(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long
    ): Response<UnivDto>

    @Headers("Content-Type: application/json")
    @PUT("universities/{id}")
    suspend fun editUniversity(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int,
        @Body university: UnivDto
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("universities/create")
    suspend fun addUniversity(
        @Header("Authorization") basicToken: String,
        @Body university: CreateUnivDto
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @DELETE("universities/{id}")
    suspend fun deleteUniversity(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int
    ): Response<ResponseBody>
}