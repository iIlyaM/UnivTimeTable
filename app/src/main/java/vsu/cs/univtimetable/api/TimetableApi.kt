package vsu.cs.univtimetable.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming
import vsu.cs.univtimetable.dto.classes.MoveClassRequest
import vsu.cs.univtimetable.dto.classes.MoveClassResponse
import vsu.cs.univtimetable.dto.classes.RequestDataDto
import vsu.cs.univtimetable.dto.classes.RequestDto
import vsu.cs.univtimetable.dto.classes.SendRequest
import vsu.cs.univtimetable.dto.classes.TimetableResponse

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

    @Headers("Content-Type: application/json")
    @Streaming
    @GET("schedule/download")
    fun downloadFile(
        @Header("Authorization") basicToken: String,
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("request/move-class")
    fun getMoveClassData(
        @Header("Authorization") basicToken: String,
    ): Call<MoveClassResponse>

    @Headers("Content-Type: application/json")
    @POST("request/move-class")
    fun move(
        @Header("Authorization") basicToken: String,
        @Body startClass: MoveClassRequest,
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("schedule/make")
    fun generate(@Header("Authorization") basicToken: String
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @GET("request/all")
    fun getLecturers(
        @Header("Authorization") basicToken: String,
    ): Call<List<RequestDto>>

    @Headers("Content-Type: application/json")
    @DELETE("schedule/reset")
    fun clearTimetable(
        @Header("Authorization") basicToken: String,
    ): Call<Void>
}