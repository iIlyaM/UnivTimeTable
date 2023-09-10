package vsu.cs.univtimetable.dto.classes

import vsu.cs.univtimetable.dto.group.GroupResponse

data class SendRequest(
    val subjectName:String,
    val groupResponse: GroupResponse,
    val subjectHourPerWeek: Int,
    val typeClass: String,
    val equipments: List<String>,
    val impossibleTime: MutableMap<String, ArrayList<String>>,
)
