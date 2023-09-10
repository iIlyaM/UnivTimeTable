package vsu.cs.univtimetable.api

import okhttp3.ResponseBody
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
import vsu.cs.univtimetable.dto.group.GroupDto
import vsu.cs.univtimetable.dto.group.GroupResponseDto

interface GroupApi {
    @Headers("Content-Type: application/json")
    @GET("faculty/v2/{facultyId}/groups")
    suspend fun getGroups(
        @Header("Authorization") basicToken: String,
        @Path("facultyId") id: Int,
        @Query("course") course: Int?,
        @Query("order") order: String?,
        @Query("groupNumber") groupNumber: Int?,
    ): Response<GroupResponseDto>

    @Headers("Content-Type: application/json")
    @GET("faculty/group/{id}")
    suspend fun getGroup(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long,
    ): Response<GroupDto>

    @Headers("Content-Type: application/json")
    @PUT("faculty/group/{id}")
    suspend fun editGroup(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long,
        @Body group: GroupDto
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("faculty/{facultyId}/group")
    suspend fun addGroup(
        @Header("Authorization") basicToken: String,
        @Path("facultyId") id: Int,
        @Body group: GroupDto
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @DELETE("faculty/group/{id}")
    suspend fun deleteGroups(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long
    ): Response<ResponseBody>
}