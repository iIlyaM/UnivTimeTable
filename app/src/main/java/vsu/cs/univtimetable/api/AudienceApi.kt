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
import vsu.cs.univtimetable.dto.audience.AudienceDto
import vsu.cs.univtimetable.dto.audience.AudienceResponse
import vsu.cs.univtimetable.dto.univ.CreateAudienceRequest

interface AudienceApi {

    @Headers("Content-Type: application/json")
    @POST("university/{univId}/faculty/{facultyId}/audience/create")
    suspend fun createAudience(
        @Header("Authorization") basicToken: String,
        @Path("univId") univId: Int,
        @Path("facultyId") facId: Int,
        @Body audience: CreateAudienceRequest
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("audience/equipments")
    suspend fun getAvailableEquipments(
        @Header("Authorization") basicToken: String
    ): Response<List<String>>

    @Headers("Content-Type: application/json")
    @GET("audience/{id}")
    suspend fun getAudience(
        @Header("Authorization") basicToken: String,
        @Path("audienceId") id: Int,
    ): Response<AudienceDto>

    @Headers("Content-Type: application/json")
    @GET("faculty/{facultyId}/audiences")
    suspend fun getAudiences(
        @Header("Authorization") basicToken: String,
        @Path("facultyId") facultyId: Int,
    ): Response<AudienceResponse>

    @Headers("Content-Type: application/json")
    @DELETE("audience/{id}")
    suspend fun deleteAudience(
        @Header("Authorization") basicToken: String,
        @Path("audienceId") id: Int,
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("audience/{id}")
    suspend fun editAudience(
        @Header("Authorization") basicToken: String,
        @Path("audienceId") id: Int,
        @Body audienceDto: AudienceDto
    ): Response<ResponseBody>
}