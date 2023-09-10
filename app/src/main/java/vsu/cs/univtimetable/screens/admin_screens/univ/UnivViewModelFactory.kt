package vsu.cs.univtimetable.screens.admin_screens.univ

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vsu.cs.univtimetable.repository.AudienceRepository
import vsu.cs.univtimetable.repository.FacultyRepository
import vsu.cs.univtimetable.repository.GroupRepository
import vsu.cs.univtimetable.repository.UnivRepository
import vsu.cs.univtimetable.repository.UserRepository
import vsu.cs.univtimetable.screens.admin_screens.audience.AudienceViewModel
import vsu.cs.univtimetable.screens.admin_screens.faculty.FacultyViewModel
import vsu.cs.univtimetable.screens.admin_screens.group.GroupViewModel
import vsu.cs.univtimetable.screens.admin_screens.users.UserViewModel
import java.lang.IllegalArgumentException

class UnivViewModelFactory(
    private val repository: Any,
    private val token: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(
                token = token,
                userRepository = repository as UserRepository
            ) as T

            modelClass.isAssignableFrom(FacultyViewModel::class.java) -> FacultyViewModel(
                token = token,
                facultyRepository = repository as FacultyRepository
            ) as T

            modelClass.isAssignableFrom(UnivViewModel::class.java) -> UnivViewModel(
                token = token,
                univRepository = repository as UnivRepository
            ) as T

            modelClass.isAssignableFrom(GroupViewModel::class.java) -> GroupViewModel(
                token = token,
                groupRepository = repository as GroupRepository
            ) as T

            modelClass.isAssignableFrom(AudienceViewModel::class.java) -> AudienceViewModel(
                token = token,
                audienceRepository = repository as AudienceRepository
            ) as T
            else -> throw IllegalArgumentException("No such ViewModel")
        }
    }
}