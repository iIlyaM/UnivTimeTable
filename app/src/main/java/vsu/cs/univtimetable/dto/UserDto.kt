package vsu.cs.univtimetable.dto

//"id": 0,
//"role": "string",
//"fullName": "string",
//"city": "string",
//"universityId": 0,
//"facultyId": 0,
//"group": 0
data class UserDto(
    val id: Int,
    val role: String,
    val city: String,
    val universityId: Int,
    val facultyId: Int,
    val group: Int
)
