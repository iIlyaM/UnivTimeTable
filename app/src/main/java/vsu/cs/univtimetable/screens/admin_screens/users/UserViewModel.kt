package vsu.cs.univtimetable.screens.admin_screens.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vsu.cs.univtimetable.dto.user.UserCreateRequest
import vsu.cs.univtimetable.dto.user.UserDisplayDto
import vsu.cs.univtimetable.repository.UserRepository

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
    val userList: LiveData<List<UserDisplayDto>> get() = _userList

    private val _errorMsg = MutableLiveData<String>()
    private val errorMsg: LiveData<String> = _errorMsg

    private val _loading = MutableLiveData<String>()
    private val loading: LiveData<String> = _loading

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
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
                onError("Error: ${response.message()}")
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
                onError("Error: ${response.message()}")
            }
        }
    }

    private fun onError(msg: String) {
        _errorMsg.postValue(msg)
    }
}