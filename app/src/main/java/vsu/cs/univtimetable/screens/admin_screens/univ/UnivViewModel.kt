package vsu.cs.univtimetable.screens.admin_screens.univ

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vsu.cs.univtimetable.dto.univ.CreateUnivDto
import vsu.cs.univtimetable.dto.univ.UnivDto
import vsu.cs.univtimetable.repository.UnivRepository
import vsu.cs.univtimetable.utils.Resource
import java.lang.Exception

class UnivViewModel(
    private val univRepository: UnivRepository,
    private val token: String
) : ViewModel() {

    private val _univList = MutableLiveData<List<UnivDto>>()
    private val _univ = MutableLiveData<UnivDto>()

    val univList: LiveData<List<UnivDto>> get() = _univList
    val univ: LiveData<UnivDto> get() = _univ

    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> = _errorMsg

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(200)
    }

    fun getUniversities(
        univName: String?,
        order: String?
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = univRepository.getUniversities(
                univName,
                order
            )
            if (response.isSuccessful) {
                _univList.postValue(response.body())
                emit(Resource.success(response.body()))
            } else {
                onError(response.code())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, exc.message.toString()))
        }
    }

    fun addUniversity(
        university: CreateUnivDto
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = univRepository.addUniversity(university)
            if (response.isSuccessful) {
                getUniversities(null, null)
                emit(Resource.success(response.body()))
            } else {
                onError(response.code())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, exc.message.toString()))
        }
    }


    fun getUniversity(
        id: Long
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = univRepository.getUniversity(
                id
            )
            if (response.isSuccessful) {
                _univ.postValue(response.body())
                emit(Resource.success(response.body()))
            } else {
                onError(response.code())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, exc.message.toString()))
        }
    }


    fun editUniversity(
        id: Int,
        university: UnivDto
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = univRepository.editUniversity(id, university)
            if (response.isSuccessful) {
                getUniversities(null, null)
                emit(Resource.success(response.body()))
            } else {
                onError(response.code())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, exc.message.toString()))
        }
    }


    fun deleteUniversity(
        id: Int,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = univRepository.deleteUniversity(id)
            if (response.isSuccessful) {
                emit(Resource.success(response.body()))
                getUniversities(null, null)
            } else {
                onError(response.code())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, exc.message.toString()))
        }
    }


    private fun onError(code: Int): String {
        var msg: String = " "
        if (code == 400) {
            msg = "Такой университет уже существует"
        }
        if (code == 403) {
            msg = "Недостаточно прав доступа для выполнения"
        }
        if (code == 404) {
            msg = "Университет по переданному id не был найден"
        }
        _errorMsg.postValue(msg)
        return msg
    }
}