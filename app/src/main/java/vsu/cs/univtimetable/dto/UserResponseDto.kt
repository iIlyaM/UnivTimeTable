package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("usersPage")
    var usersPage: UserPageModelDto<UserDisplayDto>
)
