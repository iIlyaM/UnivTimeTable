package vsu.cs.univtimetable.dto

data class GroupDto(
    val id: Long,
    val groupNumber: Int,
    val courseNumber: Int,
    val studentsAmount: Int,
    val headman: UserDisplayDto?
)