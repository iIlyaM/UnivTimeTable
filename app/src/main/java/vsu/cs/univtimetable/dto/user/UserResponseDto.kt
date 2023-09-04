package vsu.cs.univtimetable.dto.user

import com.google.gson.annotations.SerializedName
import vsu.cs.univtimetable.dto.user.UserDisplayDto
import vsu.cs.univtimetable.dto.user.UserPageModelDto

data class UserResponseDto(
    @SerializedName("usersPage")
    var usersPage: UserPageModelDto<UserDisplayDto>,
    val roles: List<String>,
    val universities: List<String>,
    val cities: List<String>
)
