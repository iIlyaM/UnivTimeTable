package vsu.cs.univtimetable.repository

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Path
import vsu.cs.univtimetable.api.AudienceApi
import vsu.cs.univtimetable.dto.audience.AudienceDto
import vsu.cs.univtimetable.dto.audience.AudienceResponse
import vsu.cs.univtimetable.dto.audience.AudienceResponseDto
import vsu.cs.univtimetable.dto.univ.CreateAudienceRequest

class AudienceRepository(
    private val audienceApi: AudienceApi,
    private val token: String
) {
    suspend fun createAudience(
        univId: Int,
        facId: Int,
        audience: CreateAudienceRequest
    ): Response<ResponseBody> {
        return audienceApi.createAudience(basicToken = "Bearer ${token}", univId, facId, audience)
    }

    suspend fun getAudiences(
        @Path("facultyId") facultyId: Int,
    ): Response<List<AudienceResponseDto>> {
        return audienceApi.getAudiences(basicToken = "Bearer ${token}", facultyId)
    }

    suspend fun getAudience(
        id: Int,
    ): Response<AudienceDto> {
        return audienceApi.getAudience(basicToken = "Bearer ${token}", id)
    }

    suspend fun editAudience(
        id: Int,
        audienceDto: AudienceDto
    ): Response<ResponseBody> {
        return audienceApi.editAudience(basicToken = "Bearer ${token}", id, audienceDto)
    }

    suspend fun deleteAudience(
        @Path("audienceId") id: Int,
    ): Response<ResponseBody> {
        return audienceApi.deleteAudience(basicToken = "Bearer ${token}", id)
    }

    suspend fun getAvailableEquipments(
    ): Response<List<String>> {
        return audienceApi.getAvailableEquipments(basicToken = "Bearer ${token}")
    }
}