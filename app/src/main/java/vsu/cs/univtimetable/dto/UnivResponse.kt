package vsu.cs.univtimetable.dto

data class UnivResponse(
    val id: Long,
    val universityName: String,
    val city: String,
    val facultyDtos: List<FacultyResponse>
)