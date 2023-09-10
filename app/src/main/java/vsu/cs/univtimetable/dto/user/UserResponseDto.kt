package vsu.cs.univtimetable.dto.user

data class UserResponseDto(
    var users: List<UserDisplayDto>,
    val roles: List<String>,
    val universities: List<String>,
    val cities: List<String>
)
