package vsu.cs.univtimetable.dto.user

data class UserCreateRequest (
    var id: Int,
    val role: String,
    val fullName: String,
    val username: String,
    val email: String,
    val city: String,
    val password: String?,
    val universityId: Long?,
    val facultyId: Long?,
    val groupId: Long?
)