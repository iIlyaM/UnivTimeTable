package vsu.cs.univtimetable.dto

import java.time.LocalTime

data class DayTime(
    val dayOfWeek: String,
    val weekType: String,
    val time: String
)
