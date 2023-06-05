package vsu.cs.univtimetable.dto

data class CreateUserResponse(
    val roles: List<String>,
    val universityResponses: List<UnivResponse>
)