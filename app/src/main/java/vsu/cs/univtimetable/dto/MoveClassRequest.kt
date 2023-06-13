package vsu.cs.univtimetable.dto

data class MoveClassRequest(
    val initClass: ClassDto,
    val classToMove: ClassDto
)
