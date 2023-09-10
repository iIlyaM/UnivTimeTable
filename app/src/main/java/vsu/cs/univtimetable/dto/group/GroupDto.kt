package vsu.cs.univtimetable.dto.group

import vsu.cs.univtimetable.dto.user.UserDisplayDto

data class GroupDto(
    val id: Long?,
    val groupNumber: Int,
    val courseNumber: Int,
    val studentsAmount: Int,
    val headman: UserDisplayDto?
)