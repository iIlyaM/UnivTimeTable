package vsu.cs.univtimetable.dto

data class RequestDataDto(
    val typesOfClass: List<String>,
    val equipments: List<String>,
    val groupsOfCourse: List<GroupResponse>
)
