package vsu.cs.univtimetable.screens.admin_screens.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import vsu.cs.univtimetable.dto.user.CreateUserResponse
import vsu.cs.univtimetable.dto.user.UserCreateRequest
import vsu.cs.univtimetable.dto.user.UserDisplayDto
import vsu.cs.univtimetable.repository.UserRepository
import vsu.cs.univtimetable.utils.Resource
import java.lang.Exception

//class UserViewModel : ViewModel() {
//    private val _userAdded = MutableLiveData<Unit>()
//    val userAdded: LiveData<Unit>
//        get() = _userAdded
//
//    fun notifyUserAdded() {
//        _userAdded.value = Unit
//    }
//}


class UserViewModel(
    private val userRepository: UserRepository,
    private val token: String
) : ViewModel() {
    private val _userList = MutableLiveData<List<UserDisplayDto>>()
    private val _user = MutableLiveData<UserCreateRequest>()
    private val _userInfo = MutableLiveData<CreateUserResponse>()

//    private val _freeHeadmenList = MutableLiveData<List<UserDisplayDto>>()
    val userList: LiveData<List<UserDisplayDto>> get() = _userList
    val user: LiveData<UserCreateRequest> get() = _user
    val userInfo: LiveData<CreateUserResponse> get() =  _userInfo

//    val freeHeadmenList: LiveData<List<UserDisplayDto>> get() = _freeHeadmenList

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
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = userRepository.getAllUsers(
                university,
                role,
                city,
                name
            )
            if (response.isSuccessful) {
                _userList.postValue(response.body()?.usersPage?.contents)
            } else {
                onError(response.code())
            }
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
//                    _group.postValue(a!!)
                emit(Resource.success(a))
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    fun getUserInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = userRepository.createUserInfo()
            if (response.isSuccessful) {
                _userInfo.postValue(response.body())
            } else {
                onError(response.code())
            }
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
    ) {
        viewModelScope.launch {
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
            } else {
                onError(response.code())
            }
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
    ) {
        viewModelScope.launch {
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
            } else {
                onError(response.code())
            }
        }
    }

//    fun getFreeHeadmen(
//        facultyId: Int
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = userRepository.getFreeHeadmen(
//                facultyId
//            )
//            if (response.isSuccessful) {
//                _userList.postValue(response.body())
//            } else {
//                onError(response.code())
//            }
//        }
//    }

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
//                    _group.postValue(a!!)
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