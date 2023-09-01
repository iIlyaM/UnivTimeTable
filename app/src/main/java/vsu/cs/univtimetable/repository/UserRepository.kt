package vsu.cs.univtimetable.repository

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Path
import vsu.cs.univtimetable.api.UserApi
import vsu.cs.univtimetable.dto.user.CreateUserResponse
import vsu.cs.univtimetable.dto.user.UserCreateRequest
import vsu.cs.univtimetable.dto.user.UserDisplayDto
import vsu.cs.univtimetable.dto.user.UserResponseDto

class UserRepository(
    private val userApi: UserApi,
    private val token: String,
) {
    suspend fun getAllUsers(
        university: String?,
        role: String?,
        city: String?,
        name: String?,
    ): Response<UserResponseDto> {
        return userApi.getUsers(basicToken = "Bearer ${token}", university, role, city, name)
    }

    suspend fun getUser(
        id: Long
    ): Response<UserCreateRequest> {
        return userApi.getUser(basicToken = "Bearer ${token}", id)
    }

    suspend fun getFreeHeadmen(
        facultyId: Int,
    ): Response<List<UserDisplayDto>> {
        return userApi.getFreeHeadmen(basicToken = "Bearer ${token}", facultyId)
    }

    suspend fun createUserInfo(

    ): Response<CreateUserResponse> {
        return userApi.createUserInfo(basicToken = "Bearer ${token}")
    }

    suspend fun addUser(
        user: UserCreateRequest
    ): Response<ResponseBody> {
        return userApi.addUser(basicToken = "Bearer ${token}", user)
    }

    suspend fun editUser(
        id: Int,
        user: UserCreateRequest
    ): Response<ResponseBody> {
        return userApi.editUser(basicToken = "Bearer ${token}", id, user)
    }


}