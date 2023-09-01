package vsu.cs.univtimetable.dto.classes

import vsu.cs.univtimetable.dto.univ.AudienceToMoveResponse

data class MoveClassResponse(
    val coursesClasses: Map<Int, List<ClassDto>>,
    val audienceToMoveResponses: List<AudienceToMoveResponse>
)