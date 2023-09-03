package vsu.cs.univtimetable.screens.admin_screens.audience

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import retrofit2.http.Body
import retrofit2.http.Path
import vsu.cs.univtimetable.dto.audience.AudienceDto
import vsu.cs.univtimetable.dto.audience.AudienceResponse
import vsu.cs.univtimetable.dto.audience.AudienceResponseDto
import vsu.cs.univtimetable.dto.univ.CreateAudienceRequest
import vsu.cs.univtimetable.dto.user.CreateUserResponse
import vsu.cs.univtimetable.dto.user.UserCreateRequest
import vsu.cs.univtimetable.dto.user.UserDisplayDto
import vsu.cs.univtimetable.repository.AudienceRepository
import vsu.cs.univtimetable.utils.Resource
import java.lang.Exception

class AudienceViewModel(
    private val audienceRepository: AudienceRepository,
    private val token: String
) : ViewModel() {
    private val _audienceList = MutableLiveData<List<AudienceResponseDto>>()
    private val _audience = MutableLiveData<CreateAudienceRequest>()
    private val _audienceInfo = MutableLiveData<AudienceDto>()

    val audienceList: LiveData<List<AudienceResponseDto>> get() = _audienceList
    val audience: LiveData<CreateAudienceRequest> get() = _audience
    val audienceInfo: LiveData<AudienceDto> get() = _audienceInfo


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

    fun getAudiences(
        facultyId: Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = audienceRepository.getAudiences(
                facultyId
            )
            if (response.isSuccessful) {
                _audienceList.postValue(response.body()?.audiences)
                emit(Resource.success(response.body()?.audiences))
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }


    fun getAudience(
        id: Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = audienceRepository.getAudience(
                id
            )
            if (response.isSuccessful) {
                val a = response.body()
                _audienceInfo.postValue(a!!)
                emit(Resource.success(a))
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    fun getAvailableEquipments() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = audienceRepository.getAvailableEquipments()
            if (response.isSuccessful) {
                emit(Resource.success(response.body()))
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }

    fun addAudience(
        univId: Int,
        facId: Int,
        audience: CreateAudienceRequest
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = audienceRepository.createAudience(
                univId,
                facId,
                audience
            )
            if (response.isSuccessful) {
                getAudiences(facId)
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
        audience: AudienceDto
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = audienceRepository.editAudience(
                id,
                audience
            )
            if (response.isSuccessful) {
                emit(Resource.success(response.body()))
            } else {
                throw Exception(response.code().toString())
            }
        } catch (exc: Exception) {
            emit(Resource.error(data = null, onError(exc.message!!.toInt())))
        }
    }


    fun deleteAudience(
        id: Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val response = audienceRepository.deleteAudience(
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
        if (code == 400) {
            msg = "Аудитория с таким номером в этом вузе на этом факультете уже существует,\n" +
                    "Не пройдена валидация"
        }
        if (code == 403) {
            msg = "Недостаточно прав доступа для выполнения"
        }
        if (code == 404) {
            msg = "Аудитория по переданному id не была найдена"
        }
        _errorMsg.postValue(msg)
        return msg
    }

}