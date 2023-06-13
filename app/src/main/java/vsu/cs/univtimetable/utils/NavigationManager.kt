package vsu.cs.univtimetable.utils

import androidx.navigation.NavController
import vsu.cs.univtimetable.R

object NavigationManager {
    fun navigateTo(decodedToken: String, navController: NavController) {
        if(decodedToken.contains("CREATE_USER", ignoreCase = true)) {
            navController.navigate(R.id.action_loginFragment_to_adminMainPageFragment)
            return
        }
        if(decodedToken.contains("MOVE_CLASS", ignoreCase = true)) {
            navController.navigate(R.id.action_loginFragment_to_lecturerMainPageFragment)
            return
        }
        if(decodedToken.contains("", ignoreCase = true)) {
            navController.navigate(R.id.action_loginFragment_to_headmanMainPageFragment)
            return
        }
    }
}