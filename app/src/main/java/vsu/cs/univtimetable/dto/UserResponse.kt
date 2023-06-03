package vsu.cs.univtimetable.dto

data class UserResponse(
    val id: Long,
    val role: String,
    val fullName: String,
    val city: String,
    val univName: String,
    val facultyName: String,
    val group: Int?
)