package vsu.cs.univtimetable.dto.user

data class UserDisplayDto(
    val id: Int,
    val role: String,
    val fullName: String,
    val city: String,
    val univName: String,
    val facultyName: String,
    val group: Int
)
