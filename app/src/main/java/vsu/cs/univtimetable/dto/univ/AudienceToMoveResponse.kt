package vsu.cs.univtimetable.dto.univ

import vsu.cs.univtimetable.dto.datetime.DayTimes

data class AudienceToMoveResponse(
    val audienceNumber: Int,
    val capacity: Long,
    val dayTimes: List<DayTimes>,
    val equipments: Set<String>
)
