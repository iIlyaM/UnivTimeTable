package vsu.cs.univtimetable.screens.admin_screens.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vsu.cs.univtimetable.dto.group.GroupDto
import vsu.cs.univtimetable.repository.GroupRepository

class GroupViewModel(
    private val groupRepository: GroupRepository,
    private val token: String
) : ViewModel() {
    private val _groupList = MutableLiveData<List<GroupDto>>()
    private val _group = MutableLiveData<GroupDto?>()

    val groupList: LiveData<List<GroupDto>> get() = _groupList
    val group: LiveData<GroupDto?> get() = _group


    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> = _errorMsg

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(200)
    }

    fun getGroups(
        facultyId: Int,
        course: Int?,
        order: String?,
        groupNumber: Int?,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = groupRepository.getGroups(
                facultyId,
                course,
                order,
                groupNumber
            )
            if (response.isSuccessful) {
                _groupList.postValue(response.body()?.groupsPage?.contents)
            } else {
                onError(response.code())
            }
        }
    }

    fun getGroup(
        id: Long
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = groupRepository.getGroup(
                id
            )
            if (response.isSuccessful) {
                val a = response.body()
                _group.postValue(a!!)
            } else {
                onError(response.code())
            }
        }
    }

    fun editGroup(
        id: Long,
        facultyId: Int,
        group: GroupDto
    ) {
        viewModelScope.launch {
            val response = groupRepository.editGroup(id, group)
            if (response.isSuccessful) {
                getGroups(facultyId, null, null, null)
            } else {
                onError(response.code())
            }
        }
    }

    fun addGroup(
        facultyId: Int,
        group: GroupDto
    ) {
        viewModelScope.launch {
            val response = groupRepository.addGroup(facultyId, group)
            if (response.isSuccessful) {
                getGroups(facultyId, null, null, null)
            } else {
                onError(response.code())
            }
        }
    }

    fun deleteGroups(
        id: Long,
        facultyId: Int
    ) {
        viewModelScope.launch {
            val response = groupRepository.deleteGroups(id)
            if (response.isSuccessful) {
                getGroups(facultyId, null, null, null)
            } else {
                onError(response.code())
            }
        }
    }

    fun clear() {
        _group.postValue(null)
    }

    private fun onError(code: Int) {
        var msg: String = " "
        if (code == 400) {
            msg = "Такая группа на этом факультете уже существует,\n" +
                    "Не пройдена валидация"
        }
        if (code == 403) {
            msg = "Недостаточно прав доступа для выполнения"
        }
        if (code == 404) {
            msg = "Староста для группы по переданному id не был найден"
        }
        _errorMsg.postValue(msg)
    }
}