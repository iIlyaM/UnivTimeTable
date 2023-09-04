package vsu.cs.univtimetable.screens.admin_screens.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import vsu.cs.univtimetable.dto.user.CreateUserResponse
import vsu.cs.univtimetable.dto.user.UserCreateRequest
import vsu.cs.univtimetable.dto.user.UserDisplayDto
import vsu.cs.univtimetable.repository.UserRepository
import vsu.cs.univtimetable.utils.Resource
import java.lang.Exception


class UserViewModel(
    private val userRepository: UserRepository,
    private val token: String
) : ViewModel() {
    private val _userList = MutableLiveData<List<UserDisplayDto>>()
    private val _user = MutableLiveData<UserCreateRequest>()
    private val _userInfo = MutableLiveData<CreateUserResponse?>()

    val userList: LiveData<List<UserDisplayDto>> get() = _userList
    val user: LiveData<UserCreateRequest> get() = _user
    val userInfo: LiveData<CreateUserResponse?> get() = _userInfo


    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> = _errorMsg

    private val _loading = MutableLiveData<String>()
    private val loading: LiveData<String> = _loading

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(200)
    }


    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun getAllUsers(
        university: String?,
        role: String?,
        city: String?,
        name: String?
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = userRepository.getAllUsers(
                university,
                role,
                city,
                name
            )
            if (response.isSuccessful) {
                _userList.postValue(response.body()?.usersPage?.contents)
                emit(Resource.success(response.body()?.usersPage?.contents))
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }


    fun getUser(
        id: Long
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = userRepository.getUser(
                id
            )
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

    fun getUserInformation() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = userRepository.createUserInfo()
            if (response.isSuccessful) {
                _userInfo.postValue(response.body())
                emit(Resource.success(response.body()))
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    fun addUser(
        id: Int,
        role: String,
        fullName: String,
        username: String,
        email: String,
        city: String,
        password: String?,
        universityId: Long?,
        facultyId: Long?,
        groupId: Long?
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = userRepository.addUser(
                UserCreateRequest(
                    id,
                    role,
                    fullName,
                    username,
                    email,
                    city,
                    password,
                    universityId,
                    facultyId,
                    groupId
                )
            )
            if (response.isSuccessful) {
                getAllUsers(null, null, null, null)
                emit(Resource.success(response.body()))
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }


    fun editUser(
        id: Int,
        role: String,
        fullName: String,
        username: String,
        email: String,
        city: String,
        password: String?,
        universityId: Long?,
        facultyId: Long?,
        groupId: Long?
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = userRepository.editUser(
                id,
                UserCreateRequest(
                    id,
                    role,
                    fullName,
                    username,
                    email,
                    city,
                    password,
                    universityId,
                    facultyId,
                    groupId
                )
            )
            if (response.isSuccessful) {
                getAllUsers(null, null, null, null)
                emit(Resource.success(response.body()))
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }


    fun getFreeHeadmen(
        facultyId: Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = userRepository.getFreeHeadmen(
                facultyId
            )
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

    fun deleteUser(
        id: Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = userRepository.deleteUser(
                id
            )
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
        if (code == 403) {
            msg = "Недостаточно прав доступа для выполнения"
        }
        if (code == 404) {
            msg = "Пользователь по переданному id не был найден"
        }
        _errorMsg.postValue(msg)
        return msg
    }
}