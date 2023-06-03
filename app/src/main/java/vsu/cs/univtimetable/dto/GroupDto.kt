package vsu.cs.univtimetable.dto

data class GroupDto(
    val id: Int?,
    val groupNumber: Int,
    val courseNumber:Int,
    val studentsAmount: Int,
    val headman: UserResponse?
)
