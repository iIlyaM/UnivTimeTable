package vsu.cs.univtimetable.dto

data class AudienceToMoveResponse(
    val audienceNumber: Int,
    val capacity: Long,
    val dayTimes: List<DayTimes>,
    val equipments: Set<String>
)
