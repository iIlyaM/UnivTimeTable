package vsu.cs.univtimetable.dto.classes

import vsu.cs.univtimetable.dto.classes.ClassDto

data class MoveClassRequest(
    val initClass: ClassDto,
    val classToMove: ClassDto
)
