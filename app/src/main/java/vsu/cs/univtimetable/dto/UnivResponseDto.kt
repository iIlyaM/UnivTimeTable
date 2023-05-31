package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName

data class UnivResponseDto (

    @SerializedName("universitiesPage")
    var universitiesPage: PageModelDto<UnivDto>
)