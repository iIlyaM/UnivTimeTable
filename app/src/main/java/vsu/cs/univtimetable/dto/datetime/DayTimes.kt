package vsu.cs.univtimetable.dto.datetime

import java.time.LocalTime

data class DayTimes(
    val dayOfWeek: String,
    val weekTimes: Map<String, List<String>>
)
