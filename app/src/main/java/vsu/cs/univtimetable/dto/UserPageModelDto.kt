package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName

data class UserPageModelDto<T>(
    @SerializedName("contents")
    var contents: List<T>,
    val currentPage: Long,
    val pageSize: Long,
    val totalPages: Long,
    val totalElements: Long
)
