package vsu.cs.univtimetable.dto.audience

data class AudienceDto(
    val id: Int,
    val audienceNumber: Int,
    val capacity: Int,
    val equipments: List<String>
)
