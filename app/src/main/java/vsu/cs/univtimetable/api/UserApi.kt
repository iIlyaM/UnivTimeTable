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
import vsu.cs.univtimetable.dto.CreateUserResponse
import vsu.cs.univtimetable.dto.UserCreateRequest
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
    @GET("user/{id}")
    fun getUser(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long
    ): Call<UserCreateRequest>

    @Headers("Content-Type: application/json")
    @PUT("user/{id}")
    fun editUser(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int,
        @Body userDto: UserCreateRequest
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @DELETE("user/{id}")
    fun deleteUser(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("user/create")
    fun addUser(
        @Header("Authorization") basicToken: String,
        @Body user: UserCreateRequest
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @GET("user/create")
    fun createUserInfo(
        @Header("Authorization") basicToken: String
    ): Call<CreateUserResponse>
//
//    @Headers("Content-Type: application/json")
//    @DELETE("universities/{id}")
//    fun deleteUniversity(
//        @Header("Authorization") basicToken: String,
//        @Path("id") id: Int
//    ): Call<Void>
}