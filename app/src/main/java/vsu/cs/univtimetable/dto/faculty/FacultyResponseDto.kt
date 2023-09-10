package vsu.cs.univtimetable.dto.faculty

import com.google.gson.annotations.SerializedName
import vsu.cs.univtimetable.dto.PageModelDto

data class FacultyResponseDto(
    @SerializedName("facultiesPage")
    var facultiesPage: PageModelDto<FacultyDto>
)
