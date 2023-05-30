package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName

data class UnivDto(
    val id: Long,

    @SerializedName("universityName")
    val universityName: String,

    @SerializedName("city")
    val city: String

)
