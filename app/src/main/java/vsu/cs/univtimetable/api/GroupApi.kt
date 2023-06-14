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
import vsu.cs.univtimetable.dto.GroupDto
import vsu.cs.univtimetable.dto.GroupResponseDto

interface GroupApi {
    @Headers("Content-Type: application/json")
    @GET("faculty/{facultyId}/groups")
    fun getGroups(
        @Header("Authorization") basicToken: String,
        @Path("facultyId") id: Int,
        @Query("course") course: Int?,
        @Query("order") order: String?,
        @Query("groupNumber") groupNumber: Int?,
    ): Call<GroupResponseDto>

    @Headers("Content-Type: application/json")
    @GET("faculty/group/{id}")
    fun getGroup(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long,
    ): Call<GroupDto>

    @Headers("Content-Type: application/json")
    @PUT("faculty/group/{id}")
    fun editGroup(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long,
        @Body group: GroupDto
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("faculty/{facultyId}/group")
    fun addGroup(
        @Header("Authorization") basicToken: String,
        @Path("facultyId") id: Int,
        @Body group: GroupDto
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @DELETE("faculty/group/{id}")
    fun deleteGroups(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long
    ): Call<Void>
}