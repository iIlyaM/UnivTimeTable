package vsu.cs.univtimetable.dto.group

import com.google.gson.annotations.SerializedName
import vsu.cs.univtimetable.dto.PageModelDto

data class GroupResponseDto (
    @SerializedName("groups")
    var groups: List<GroupDto>,
    var courses: List<String>
)