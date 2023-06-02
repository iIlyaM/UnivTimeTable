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
import vsu.cs.univtimetable.dto.UserDisplayDto
import vsu.cs.univtimetable.dto.UserDto
import vsu.cs.univtimetable.dto.UserResponseDto

interface UserApi {
    @Headers("Content-Type: application/json")
    @GET("users")
    fun getUsers(
        @Header("Authorization") basicToken: String,
        @Query("university") university: String?,
        @Query("role") role: String?,
        @Query("city") city: String?,
        @Query("name") name: String?,
    ): Call<UserResponseDto>

    @Headers("Content-Type: application/json")
    @PUT("users/{id}")
    fun editUser(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int,
        @Body university: UserDto
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("user/create")
    fun addUser(
        @Header("Authorization") basicToken: String,
        @Body user: CreateUnivDto
    ): Call<Void>
//
//    @Headers("Content-Type: application/json")
//    @DELETE("universities/{id}")
//    fun deleteUniversity(
//        @Header("Authorization") basicToken: String,
//        @Path("id") id: Int
//    ): Call<Void>
}