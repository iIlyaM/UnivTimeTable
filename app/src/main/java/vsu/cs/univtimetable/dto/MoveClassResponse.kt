package vsu.cs.univtimetable.dto

data class MoveClassResponse(
    val coursesClasses: Map<Int, List<MoveClassDto>>,
    val possibleTimesInAudience: Map<Int, List<DayTimes>>
)
