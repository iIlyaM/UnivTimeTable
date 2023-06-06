package vsu.cs.univtimetable.dto

data class MoveClassDto (
    val groups: Set<Int>,
    val groupClasses: List<ClassDto>
)