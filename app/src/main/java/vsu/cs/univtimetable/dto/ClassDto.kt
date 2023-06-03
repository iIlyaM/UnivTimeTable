package vsu.cs.univtimetable.dto


data class ClassDto(
    val subjectName: String,
    val startTime: StartTimeDto,
    val audience: Int,
    val dayOfWeek: String,
    val typeOfClass: String,
    val weekType: String,
    val courseNumber: Int,
    val groupsNumber: List<Int>
)
