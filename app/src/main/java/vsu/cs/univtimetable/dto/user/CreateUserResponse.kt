package vsu.cs.univtimetable.dto.user

import vsu.cs.univtimetable.dto.univ.UnivResponse

data class CreateUserResponse(
    val roles: List<String>,
    val universityResponses: List<UnivResponse>
)