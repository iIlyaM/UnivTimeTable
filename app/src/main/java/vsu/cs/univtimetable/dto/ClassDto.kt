package vsu.cs.univtimetable.dto


data class ClassDto(
    val subjectName: String,
    val startTime: String,
    val endTime: String,
    val audience: Int,
    val dayOfWeek: String,
    val typeOfClass: String,
    val weekType: String,
    val courseNumber: Int,
    val groupsNumber: List<Int>,
    val capacity: Int,
    val equipments: List<String>
)
