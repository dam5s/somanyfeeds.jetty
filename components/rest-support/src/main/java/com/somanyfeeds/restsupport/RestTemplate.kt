package com.somanyfeeds.restsupport

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import kotlin.reflect.KClass

class RestTemplate {

    val client = OkHttpClient()
    val objectMapper = ObjectMapper()

    fun get(url: String): RestResult<String> {
        return get(url, { response ->
            response.body().string()
        })
    }

    fun <T : Any> get(url: String, klass: KClass<T>): RestResult<T> {
        return get(url, { response ->
            objectMapper.readValue(response.body().byteStream(), klass.java)
        })
    }

    private fun <T : Any> get(url: String, successHandler: (Response) -> T): RestResult<T> {
        val request = Request.Builder()
            .get()
            .url(url)
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            return RestResult.Success(successHandler(response))
        }

        return RestResult.Error(response.message())
    }
}

sealed class RestResult<T> {
    class Success<T>(val value: T) : RestResult<T>()
    class Error<T>(val error: String) : RestResult<T>()
}
