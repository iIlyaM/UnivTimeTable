package vsu.cs.univtimetable.dto.classes

import vsu.cs.univtimetable.dto.group.GroupResponse

data class RequestDataDto(
    val typesOfClass: List<String>,
    val equipments: List<String>,
    val groupsOfCourse: List<GroupResponse>
)
