package vsu.cs.univtimetable.dto

data class CreateAudienceRequest (
    var audienceNumber: Int,
    var capacity: Int,
    var equipments: List<String>
)