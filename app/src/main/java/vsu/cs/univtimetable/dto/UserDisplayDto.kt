package vsu.cs.univtimetable.dto

data class UserDisplayDto(
    val id: Int,
    val role: String,
    val fullName: String,
    val email: String,
    val city: String,
    val university: String,
    val faculty: String,
    val group: Int
)
