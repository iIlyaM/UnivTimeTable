package vsu.cs.univtimetable.screens.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import vsu.cs.univtimetable.dto.auth.AuthRequestDto
import vsu.cs.univtimetable.repository.AuthRepository
import vsu.cs.univtimetable.utils.Resource
import java.lang.Exception

class LoginViewModel(
    private val authRepository: AuthRepository
): ViewModel() {


    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> = _errorMsg

    fun login(
        login: AuthRequestDto,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = authRepository.login(login)
            if (response.isSuccessful) {
                val a = response.body()
                emit(Resource.success(a))
            } else {
                throw Exception(response.code().toString())

            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    private fun onError(code: Int): String {
        var msg: String = " "
        if (code == 400) {
            msg = "Неверные учетные данные пользователя"
        }
        if (code == 404) {
            msg = "Пользователь по переданному email не был найден"
        }
        _errorMsg.postValue(msg)
        return msg
    }
}