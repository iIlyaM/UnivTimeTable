package vsu.cs.univtimetable.repository

import retrofit2.Response
import vsu.cs.univtimetable.api.UserAuthApi
import vsu.cs.univtimetable.dto.auth.AuthRequestDto
import vsu.cs.univtimetable.dto.auth.AuthResponseDto

class AuthRepository(
    private val userAuthApi: UserAuthApi
) {
    suspend fun login(
        login: AuthRequestDto,
    ): Response<AuthResponseDto> {
        return userAuthApi.login(login)
    }

}