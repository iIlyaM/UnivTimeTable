package vsu.cs.univtimetable.repository

import okhttp3.ResponseBody
import retrofit2.Response
import vsu.cs.univtimetable.api.UserApi
import vsu.cs.univtimetable.dto.user.UserCreateRequest
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
        return userApi.getUsers(basicToken="Bearer ${token}", university, role, city, name)
    }

    suspend fun addUser(
        user: UserCreateRequest
    ): Response<ResponseBody> {
        return userApi.addUser(basicToken="Bearer ${token}", user)
    }


}