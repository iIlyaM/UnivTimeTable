package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName

class UnivResponseDto {
    @SerializedName("contents")
    val contents: ArrayList<UnivDto> = arrayListOf()
}