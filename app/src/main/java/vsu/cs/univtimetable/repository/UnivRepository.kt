package vsu.cs.univtimetable.repository

import okhttp3.ResponseBody
import retrofit2.Response
import vsu.cs.univtimetable.api.UnivApi
import vsu.cs.univtimetable.dto.univ.CreateUnivDto
import vsu.cs.univtimetable.dto.univ.UnivDto
import vsu.cs.univtimetable.dto.univ.UnivResponseDto
import vsu.cs.univtimetable.dto.user.UserCreateRequest

class UnivRepository(
    private val univApi: UnivApi,
    private val token: String,
) {

    suspend fun addUniversity(
        university: CreateUnivDto
    ): Response<ResponseBody> {
        return univApi.addUniversity(basicToken = "Bearer ${token}", university)
    }

    suspend fun editUniversity(
        id: Int,
        university: UnivDto
    ): Response<ResponseBody> {
        return univApi.editUniversity(basicToken = "Bearer ${token}", id, university)
    }

    suspend fun getUniversities(
        univName: String?,
        order: String?
    ): Response<List<UnivDto>> {
        return univApi.getUniversities(basicToken = "Bearer ${token}", univName, order)
    }

    suspend fun getUniversity(
        id: Long
    ): Response<UnivDto> {
        return univApi.getUniversity(basicToken = "Bearer ${token}", id)
    }

    suspend fun deleteUniversity(
        id: Int
    ): Response<ResponseBody> {
        return univApi.deleteUniversity(basicToken = "Bearer ${token}", id)
    }
}