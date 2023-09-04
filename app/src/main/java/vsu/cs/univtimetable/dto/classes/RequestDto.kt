package vsu.cs.univtimetable.dto.classes

import vsu.cs.univtimetable.dto.user.UserDto

data class RequestDto(
    val subjectName: String,
    val subjectHourPerWeek: Float,
    val typeClass: String,
    val userDto: UserDto
)
