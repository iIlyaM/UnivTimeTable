package vsu.cs.univtimetable.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName("accessToken")
    val token: String,

    @SerializedName("refreshToken")
    val refreshToken: String
)
