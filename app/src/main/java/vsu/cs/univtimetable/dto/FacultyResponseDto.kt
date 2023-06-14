package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName

data class FacultyResponseDto(
    @SerializedName("facultiesPage")
    var facultiesPage: PageModelDto<FacultyDto>
)
