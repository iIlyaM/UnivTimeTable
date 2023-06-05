package vsu.cs.univtimetable.dto

import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("usersPage")
    var usersPage: UserPageModelDto<UserDisplayDto>,
    val roles: List<String>,
    val universities: List<String>,
    val cities: List<String>
)
