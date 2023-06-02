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
import vsu.cs.univtimetable.dto.CreateFacultyDto
import vsu.cs.univtimetable.dto.FacultyDto
import vsu.cs.univtimetable.dto.FacultyResponseDto

interface FacultyApi {

    @Headers("Content-Type: application/json")
    @GET("university/{univId}/faculties")
    fun getFaculties(
        @Header("Authorization") basicToken: String,
        @Path("univId") id: Int,
        @Query("name") name: String?,
        @Query("order") order: String?
    ): Call<FacultyResponseDto>

    @Headers("Content-Type: application/json")
    @POST("university/{univId}/faculty/create")
    fun addFaculty(
        @Header("Authorization") basicToken: String,
        @Path("univId") id: Int,
        @Body faculty: CreateFacultyDto
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @PUT("university/faculty/{id}")
    fun editFaculty(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int,
        @Body faculty: FacultyDto
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @DELETE("university/faculty/{id}")
    fun deleteFaculty(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int
    ): Call<Void>
}