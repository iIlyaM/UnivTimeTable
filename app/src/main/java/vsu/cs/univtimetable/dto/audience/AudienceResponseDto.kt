package vsu.cs.univtimetable.dto.audience

data class AudienceResponseDto(
    val id: Long,
    val audienceNumber: Int,
    val capacity: Int,
    val equipments: List<String>,
    val university: String,
    val faculty: String
)
