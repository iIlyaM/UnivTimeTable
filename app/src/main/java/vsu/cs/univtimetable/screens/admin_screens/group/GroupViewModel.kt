package vsu.cs.univtimetable.screens.admin_screens.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vsu.cs.univtimetable.dto.group.GroupDto
import vsu.cs.univtimetable.repository.GroupRepository
import vsu.cs.univtimetable.utils.Resource
import java.lang.Exception

class GroupViewModel(
    private val groupRepository: GroupRepository,
    private val token: String
) : ViewModel() {
    private val _groupList = MutableLiveData<List<GroupDto>>()
    private val _group = MutableLiveData<GroupDto?>()

    val groupList: LiveData<List<GroupDto>> = _groupList
    val groupIMmutable: LiveData<GroupDto?> = _group


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
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = groupRepository.getGroups(
                facultyId,
                course,
                order,
                groupNumber
            )
            if (response.isSuccessful) {
                val a = response.body()
                _groupList.postValue(response.body()?.groupsPage?.contents)
                emit(Resource.success(a))
            } else {
                onError(response.code())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, exc.message.toString()))
        }
    }

    fun getGroup(
        id: Long
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = groupRepository.getGroup(
                id
            )
            if (response.isSuccessful) {
                val a = response.body()
                emit(Resource.success(a))
            } else {
                throw Exception(onError(response.code()))

            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, exc.message.toString()))
        }
    }


    fun editGroup(
        id: Long,
        facultyId: Int,
        group: GroupDto
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = groupRepository.editGroup(id, group)
            if (response.isSuccessful) {
                getGroups(facultyId, null, null, null)
                val a = response.body()
                emit(Resource.success(a))
                getGroups(facultyId, null, null, null)
            } else {
                throw Exception(response.code().toString())

            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    fun addGroup(
        facultyId: Int,
        group: GroupDto
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = groupRepository.addGroup(facultyId, group)
            if (response.isSuccessful) {
                val a = response.body()
                emit(Resource.success(a))
                getGroups(facultyId, null, null, null)
            } else {
                throw Exception(response.code().toString())

            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    fun deleteGroups(
        id: Long,
        facultyId: Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = groupRepository.deleteGroups(id)
            if (response.isSuccessful) {
                val a = response.body()
                emit(Resource.success(a))
                getGroups(facultyId, null, null, null)
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
            msg = "Такая группа на этом факультете уже существует,\n" +
                    "Не пройдена валидация"
        }
        if (code == 403) {
            msg = "Недостаточно прав доступа для выполнения"
        }
        if (code == 404) {
            msg = "Староста для группы не установлен"
        }
        _errorMsg.postValue(msg)
        return msg
    }
}