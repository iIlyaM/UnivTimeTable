package vsu.cs.univtimetable

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

object RequestInterceptor : Interceptor {
override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val requestBody = request.body()
    val buffer = Buffer()
    requestBody?.writeTo(buffer)
    val body = buffer.readUtf8()
    Log.d("API Request", "${request.url()} $body")
    return chain.proceed(request)
}
}