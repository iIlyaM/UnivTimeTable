package vsu.cs.univtimetable.dto.audience

import com.google.gson.annotations.SerializedName
import vsu.cs.univtimetable.dto.PageModelDto
import vsu.cs.univtimetable.dto.faculty.FacultyDto

data class AudienceResponse(
    @SerializedName("audiencesPage")
    var audiencesPage: PageModelDto<AudienceResponseDto>
)
