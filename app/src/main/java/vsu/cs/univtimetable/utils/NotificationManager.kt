package vsu.cs.univtimetable.utils

import android.app.ProgressDialog
import android.os.Handler
import android.widget.Toast
import android.content.Context


object NotificationManager {
    fun showToastNotification(context: Context, message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(context, message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }

    fun setLoadingDialog(pDialog: ProgressDialog) {
        pDialog.setMessage("Загрузка...пожалуйста подождите")
        pDialog.setCancelable(false)
        pDialog.show()
    }
}