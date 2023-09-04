package vsu.cs.univtimetable.dto.univ

data class CreateAudienceRequest (
    var audienceNumber: Int,
    var capacity: Int,
    var equipments: List<String>
)