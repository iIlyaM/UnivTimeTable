package vsu.cs.univtimetable.screens.admin_screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vsu.cs.univtimetable.dto.UserDisplayDto

class UserViewModel : ViewModel() {
    private val _userList = MutableLiveData<List<UserDisplayDto>>()
    val userList: LiveData<List<UserDisplayDto>> get() = _userList

    init {
        _userList.value = emptyList()
    }

    fun addUser(user: UserDisplayDto) {
        val updatedList = (_userList.value ?: emptyList()) + user
        _userList.postValue(updatedList)
    }
}