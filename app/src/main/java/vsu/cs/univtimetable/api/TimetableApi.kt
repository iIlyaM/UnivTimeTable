package vsu.cs.univtimetable.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import vsu.cs.univtimetable.dto.TimetableResponse

interface TimetableApi {

    @Headers("Content-Type: application/json")
    @GET("schedule")
    fun getTimetable(
        @Header("Authorization") basicToken: String,
    ): Call<TimetableResponse>
}