package vsu.cs.univtimetable.dto

data class TimetableResponse(
    val classes: MutableMap<String, MutableMap<String, List<ClassDto>>>
)
