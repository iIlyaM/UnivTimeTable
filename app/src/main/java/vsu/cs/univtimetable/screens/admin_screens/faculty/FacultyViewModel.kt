package vsu.cs.univtimetable.screens.admin_screens.faculty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vsu.cs.univtimetable.dto.faculty.FacultyDto
import vsu.cs.univtimetable.dto.univ.CreateFacultyDto
import vsu.cs.univtimetable.repository.FacultyRepository
import vsu.cs.univtimetable.utils.Resource
import java.lang.Exception

class FacultyViewModel(
    private val facultyRepository: FacultyRepository,
    private val token: String
) : ViewModel() {
    private val _facultyList = MutableLiveData<List<FacultyDto>>()
    private val _faculty = MutableLiveData<FacultyDto>()

    val facultyList: LiveData<List<FacultyDto>> get() = _facultyList
    val faculty: LiveData<FacultyDto> get() = _faculty

    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> = _errorMsg

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        onError(200)
    }

    fun addFaculty(
        id: Int,
        faculty: CreateFacultyDto
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = facultyRepository.addFaculty(id, faculty)
            if (response.isSuccessful) {
                emit(Resource.success(response.body()))
                getFaculties(id, null, null)
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    fun editFaculty(
        id: Int,
        univId: Int,
        faculty: FacultyDto
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = facultyRepository.editFaculty(
                id,
                faculty
            )
            if (response.isSuccessful) {
                getFaculties(univId, null, null)
                emit(Resource.success(response.body()))
//                getFaculties(univId, null, null)
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    fun getFaculties(
        id: Int,
        name: String?,
        order: String?
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = facultyRepository.getFaculties(
                id,
                name,
                order
            )
            if (response.isSuccessful) {
                val a = response.body()
                _facultyList.postValue(a!!)
                emit(Resource.success(a))
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    fun getFaculty(
        id: Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = facultyRepository.getFaculty(
                id
            )
            if (response.isSuccessful) {
                emit(Resource.success(response.body()))
                _faculty.postValue(response.body())
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    fun deleteFaculty(
        id: Int,
        univId: Int,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = facultyRepository.deleteFaculty(id)
            if (response.isSuccessful) {
                emit(Resource.success(response.body()))
                getFaculties(univId, null, null)
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
            msg = "Такой факультет уже существует"
        }
        if (code == 403) {
            msg = "Недостаточно прав доступа для выполнения"
        }
        if (code == 404) {
            msg = "Факультет по переданному id не был найден"
        }
        _errorMsg.postValue(msg)
        return msg
    }
}