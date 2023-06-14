package vsu.cs.univtimetable.dto

data class RequestDto(
    val subjectName: String,
    val subjectHourPerWeek: Float,
    val typeClass: String,
    val userDto: UserDto
)
