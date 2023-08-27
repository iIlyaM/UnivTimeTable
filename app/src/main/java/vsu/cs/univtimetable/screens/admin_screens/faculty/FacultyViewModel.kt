package vsu.cs.univtimetable.screens.admin_screens.faculty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vsu.cs.univtimetable.dto.faculty.FacultyDto
import vsu.cs.univtimetable.dto.univ.CreateFacultyDto
import vsu.cs.univtimetable.repository.FacultyRepository

class FacultyViewModel(
    private val facultyRepository: FacultyRepository,
    private val token: String
): ViewModel() {
    private val _facultyList = MutableLiveData<List<FacultyDto>>()
    private val _faculty = MutableLiveData<FacultyDto>()

    val facultyList: LiveData<List<FacultyDto>> get() = _facultyList
    val faculty: LiveData<FacultyDto> get() = _faculty

    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> = _errorMsg

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(200)
    }

    fun addFaculty(
        id: Int,
        faculty: CreateFacultyDto
    ) {
        viewModelScope.launch {
            val response = facultyRepository.addFaculty(id, faculty)
            if (response.isSuccessful) {
                getFaculties(id, null, null)
            } else {
                onError(response.code())
            }
        }
    }

    fun editFaculty(
        id: Int,
        univId: Int,
        faculty: FacultyDto
    ) {
        viewModelScope.launch {
            val response = facultyRepository.editFaculty(
                id,
                faculty
            )
            if (response.isSuccessful) {
                getFaculties(univId, null, null)
            } else {
                onError(response.code())
            }
        }
    }

    fun getFaculties(
        id: Int,
        name: String?,
        order: String?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = facultyRepository.getFaculties(
                id,
                name,
                order
            )
            if (response.isSuccessful) {
                _facultyList.postValue(response.body()?.facultiesPage?.contents)
            } else {
                onError(response.code())
            }
        }
    }

    fun getFaculty(
        id: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = facultyRepository.getFaculty(
                id
            )
            if (response.isSuccessful) {
                _faculty.postValue(response.body())
            } else {
                onError(response.code())
            }
        }
    }

    fun deleteFaculty(
        id: Int,
        univId: Int,
    ) {
        viewModelScope.launch {
            val response = facultyRepository.deleteFaculty(id)
            if (response.isSuccessful) {
                getFaculties(univId, null, null)
            } else {
                onError(response.code())
            }
        }
    }


    private fun onError(code: Int) {
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
    }
}