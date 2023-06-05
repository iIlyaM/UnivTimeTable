package vsu.cs.univtimetable.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import vsu.cs.univtimetable.dto.RequestDataDto
import vsu.cs.univtimetable.dto.SendRequest
import vsu.cs.univtimetable.dto.TimetableResponse

interface TimetableApi {

    @Headers("Content-Type: application/json")
    @GET("schedule")
    fun getTimetable(
        @Header("Authorization") basicToken: String,
    ): Call<TimetableResponse>

    @Headers("Content-Type: application/json")
    @GET("request/send")
    fun getRequestData(
        @Header("Authorization") basicToken: String,
    ): Call<RequestDataDto>

    @Headers("Content-Type: application/json")
    @POST("request/send")
    fun postSubject(
        @Header("Authorization") basicToken: String,
        @Body request: SendRequest
    ): Call<Void>
}