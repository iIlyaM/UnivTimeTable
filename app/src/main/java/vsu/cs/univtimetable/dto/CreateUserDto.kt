package vsu.cs.univtimetable.dto

data class CreateUserDto(
    val id: Int,
    val role: String,
    val fullName: String,
    val username: String,
    val email: String,
    val city: String,
    val password: String,
    val universityId: Int,
    val facultyId: Int,
    val groupId: Int
)
