package vsu.cs.univtimetable.dto

data class MoveClassResponse(
    val coursesClasses: Map<Int, List<ClassDto>>,
    val audienceToMoveResponses: List<AudienceToMoveResponse>
)