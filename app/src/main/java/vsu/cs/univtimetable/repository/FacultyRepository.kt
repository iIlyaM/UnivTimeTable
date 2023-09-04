package vsu.cs.univtimetable.repository

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Path
import vsu.cs.univtimetable.api.FacultyApi
import vsu.cs.univtimetable.dto.faculty.FacultyDto
import vsu.cs.univtimetable.dto.faculty.FacultyResponseDto
import vsu.cs.univtimetable.dto.univ.CreateFacultyDto

class FacultyRepository(
    private val facultyApi: FacultyApi,
    private val token: String
) {
    suspend fun addFaculty(
        id: Int,
        faculty: CreateFacultyDto
    ): Response<ResponseBody> {
        return facultyApi.addFaculty(basicToken = "Bearer ${token}", id, faculty)
    }

    suspend fun editFaculty(
        id: Int,
        faculty: FacultyDto
    ): Response<ResponseBody> {
        return facultyApi.editFaculty(basicToken = "Bearer ${token}", id, faculty)
    }

    suspend fun getFaculties(
        id: Int,
        name: String?,
        order: String?
    ): Response<FacultyResponseDto> {
        return facultyApi.getFaculties(basicToken = "Bearer ${token}", id, name, order)
    }

    suspend fun getFaculty(
        id: Int
    ): Response<FacultyDto> {
        return facultyApi.getFaculty(basicToken = "Bearer ${token}", id)
    }

    suspend fun deleteFaculty(
        id: Int
    ): Response<ResponseBody> {
        return facultyApi.deleteFaculty(basicToken = "Bearer ${token}", id)
    }

}