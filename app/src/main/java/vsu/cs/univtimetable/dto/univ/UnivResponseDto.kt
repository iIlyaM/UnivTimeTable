package vsu.cs.univtimetable.dto.univ

import com.google.gson.annotations.SerializedName
import vsu.cs.univtimetable.dto.PageModelDto

data class UnivResponseDto (

    var universitiesPage: List<UnivDto>
)