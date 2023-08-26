package vsu.cs.univtimetable.screens.admin_screens.univ

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vsu.cs.univtimetable.repository.UnivRepository
import vsu.cs.univtimetable.repository.UserRepository
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

            modelClass.isAssignableFrom(UnivViewModel::class.java) -> UnivViewModel(
                token = token,
                univRepository = repository as UnivRepository
            ) as T
            else -> throw IllegalArgumentException("No such ViewModel")
        }
    }
}