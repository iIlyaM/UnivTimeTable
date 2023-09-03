package vsu.cs.univtimetable.dto.audience

import com.google.gson.annotations.SerializedName
import vsu.cs.univtimetable.dto.PageModelDto

data class AudienceResponse(
    @SerializedName("audiences")
    var audiences: List<AudienceResponseDto>
)
