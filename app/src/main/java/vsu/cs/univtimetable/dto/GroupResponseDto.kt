package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName

data class GroupResponseDto (
    @SerializedName("groupsPage")
    var groupsPage: PageModelDto<GroupResponseDto>
)