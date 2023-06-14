package vsu.cs.univtimetable.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import vsu.cs.univtimetable.dto.CreateAudienceRequest

interface AudienceApi {

    @Headers("Content-Type: application/json")
    @POST("university/{univId}/faculty/{facultyId}/audience/create")
    fun createAudience(
        @Header("Authorization") basicToken: String,
        @Path("univId") univId: Int,
        @Path("facultyId") facId: Int,
        @Body audience: CreateAudienceRequest
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @GET("audience/equipments")
    fun getAvailableEquipments(
        @Header("Authorization") basicToken: String
    ): Call<List<String>>
}