package vsu.cs.univtimetable.utils.permissions_utils

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import okhttp3.ResponseBody
import retrofit2.Response

object PermissionsHandler {

    fun loadFile(
        contentResolver: ContentResolver,
        resolver: ContentResolver,
        contentValues: ContentValues,
        response: Response<ResponseBody>
    ) {
        val uri = resolver.insert(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            contentValues
        )
        val outputStream = uri?.let { contentResolver.openOutputStream(it) }

        val inputStream = response.body()?.byteStream()
        inputStream?.use { input ->
            outputStream?.use { output ->
                input.copyTo(output)
            }
        }
        contentValues.clear()
        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
        uri?.let { contentResolver.update(it, contentValues, null, null) }

    }

    fun checkPermissionsButton(context: Context, button: AppCompatButton) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            button.visibility = View.INVISIBLE
        }
    }
}