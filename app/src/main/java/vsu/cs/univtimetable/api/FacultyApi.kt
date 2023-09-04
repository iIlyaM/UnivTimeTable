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
import vsu.cs.univtimetable.dto.univ.CreateFacultyDto
import vsu.cs.univtimetable.dto.faculty.FacultyDto
import vsu.cs.univtimetable.dto.faculty.FacultyResponseDto

interface FacultyApi {

    @Headers("Content-Type: application/json")
    @GET("university/{univId}/faculties")
    suspend fun getFaculties(
        @Header("Authorization") basicToken: String,
        @Path("univId") id: Int,
        @Query("name") name: String?,
        @Query("order") order: String?
    ): Response<FacultyResponseDto>

    @Headers("Content-Type: application/json")
    @GET("university/faculty/{id}")
    suspend fun getFaculty(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int
    ): Response<FacultyDto>

    @Headers("Content-Type: application/json")
    @POST("university/{univId}/faculty/create")
    suspend fun addFaculty(
        @Header("Authorization") basicToken: String,
        @Path("univId") id: Int,
        @Body faculty: CreateFacultyDto
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @PUT("university/faculty/{id}")
    suspend fun editFaculty(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int,
        @Body faculty: FacultyDto
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @DELETE("university/faculty/{id}")
    suspend fun deleteFaculty(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int
    ): Response<ResponseBody>
}