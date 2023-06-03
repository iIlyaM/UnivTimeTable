package vsu.cs.univtimetable.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import vsu.cs.univtimetable.dto.UserResponse

interface UserApi {
    @Headers("Content-Type: application/json")
    @GET("faculty/{facultyId}/headmen")
    fun getFreeHeadmen(
        @Header("Authorization") basicToken: String,
        @Path("facultyId") id: Int,
    ): Call<List<UserResponse>>
}