package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ImpossibleTimeDto(
    val impossibleTime: MutableMap<String, ArrayList<String>>
) : Serializable
