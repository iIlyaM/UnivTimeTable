package vsu.cs.univtimetable.dto

data class FacultyResponse (
    val id: Long,
    val name: String,
    val groups: List<GroupDto>
)