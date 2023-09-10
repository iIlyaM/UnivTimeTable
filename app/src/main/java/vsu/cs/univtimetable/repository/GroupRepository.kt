package vsu.cs.univtimetable.repository

import okhttp3.ResponseBody
import retrofit2.Response
import vsu.cs.univtimetable.api.GroupApi
import vsu.cs.univtimetable.dto.group.GroupDto
import vsu.cs.univtimetable.dto.group.GroupResponseDto

class GroupRepository(
    private val groupApi: GroupApi,
    private val token: String
) {

    suspend fun getGroups(
        id: Int,
        course: Int?,
        order: String?,
        groupNumber: Int?,
    ): Response<GroupResponseDto> {
        return groupApi.getGroups(basicToken = "Bearer ${token}", id, course, order, groupNumber)
    }

    suspend fun getGroup(
        id: Long
    ): Response<GroupDto> {
        return groupApi.getGroup(basicToken = "Bearer ${token}", id)
    }

    suspend fun editGroup(
        id: Long,
        group: GroupDto
    ): Response<ResponseBody> {
        return groupApi.editGroup(basicToken = "Bearer ${token}", id, group)
    }

    suspend fun addGroup(
        id: Int,
        group: GroupDto
    ): Response<ResponseBody> {
        return groupApi.addGroup(basicToken = "Bearer ${token}", id, group)
    }

    suspend fun deleteGroups(
        id: Long
    ): Response<ResponseBody> {
        return groupApi.deleteGroups(basicToken = "Bearer ${token}", id)
    }
}