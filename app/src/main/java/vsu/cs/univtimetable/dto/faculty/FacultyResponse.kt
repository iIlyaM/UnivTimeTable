package vsu.cs.univtimetable.dto.faculty

import vsu.cs.univtimetable.dto.group.GroupDto

data class FacultyResponse (
    val id: Long,
    val name: String,
    val groups: List<GroupDto>
)