package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName

data class AuthRequestDto(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String
)
