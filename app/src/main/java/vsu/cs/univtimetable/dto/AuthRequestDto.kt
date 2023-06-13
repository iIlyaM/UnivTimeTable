package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName

data class AuthRequestDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
