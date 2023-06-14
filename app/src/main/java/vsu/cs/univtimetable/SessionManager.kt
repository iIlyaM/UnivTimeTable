package vsu.cs.univtimetable

import android.content.Context
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT

object SessionManager {

    const val USER_TOKEN = ""


    fun saveAuthToken(context: Context, token: String) {
        saveString(context, USER_TOKEN, token)
    }


    fun getToken(context: Context): String? {
        return getString(context, USER_TOKEN)
    }

    fun decodeToken(token: String): String {
        val jwt = JWT(token)
        val claim = jwt.getClaim("authorities").asList(String::class.java)
        return claim.toString()
    }

    fun saveString(context: Context, key: String, value: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()

    }

    fun getString(context: Context, key: String): String? {
        val prefs: SharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        return prefs.getString(this.USER_TOKEN, null)
    }

    fun clearData(context: Context){
        val editor = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
    }
}