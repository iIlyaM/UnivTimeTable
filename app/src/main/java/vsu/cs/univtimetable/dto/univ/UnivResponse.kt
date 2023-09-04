package vsu.cs.univtimetable.dto.univ

import vsu.cs.univtimetable.dto.faculty.FacultyResponse

data class UnivResponse(
    val id: Long,
    val universityName: String,
    val city: String,
    val facultyDtos: List<FacultyResponse>
)