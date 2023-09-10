package vsu.cs.univtimetable.dto.classes

import vsu.cs.univtimetable.dto.classes.ClassDto

data class TimetableResponse(
    val classes: MutableMap<String, MutableMap<String, List<ClassDto>>>
)
