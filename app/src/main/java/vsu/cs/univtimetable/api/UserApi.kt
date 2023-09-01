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
import vsu.cs.univtimetable.dto.user.CreateUserResponse
import vsu.cs.univtimetable.dto.user.UserCreateRequest
import vsu.cs.univtimetable.dto.user.UserDisplayDto
import vsu.cs.univtimetable.dto.user.UserResponseDto

interface UserApi {
    @Headers("Content-Type: application/json")
    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") basicToken: String,
        @Query("university") university: String?,
        @Query("role") role: String?,
        @Query("city") city: String?,
        @Query("name") name: String?,
    ): Response<UserResponseDto>

    @Headers("Content-Type: application/json")
    @GET("user/{id}")
    suspend fun getUser(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Long
    ): Response<UserCreateRequest>

    @Headers("Content-Type: application/json")
    @PUT("user/{id}")
    suspend fun editUser(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int,
        @Body userDto: UserCreateRequest
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @DELETE("user/{id}")
    fun deleteUser(
        @Header("Authorization") basicToken: String,
        @Path("id") id: Int
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("user/create")
    suspend fun addUser(
        @Header("Authorization") basicToken: String,
        @Body user: UserCreateRequest
    ): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("user/create")
    suspend fun createUserInfo(
        @Header("Authorization") basicToken: String
    ): Response<CreateUserResponse>
//
//    @Headers("Content-Type: application/json")
//    @DELETE("universities/{id}")
//    fun deleteUniversity(
//        @Header("Authorization") basicToken: String,
//        @Path("id") id: Int
//    ): Call<Void>

    @Headers("Content-Type: application/json")
    @GET("faculty/{facultyId}/headmen")
    suspend fun getFreeHeadmen(
        @Header("Authorization") basicToken: String,
        @Path("facultyId") id: Int,
    ): Response<List<UserDisplayDto>>
}