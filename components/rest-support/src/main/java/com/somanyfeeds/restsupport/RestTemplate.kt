package com.somanyfeeds.restsupport

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.*
import kotlin.reflect.KClass

class RestTemplate {

    val client = OkHttpClient()
    val objectMapper = jacksonObjectMapper().apply {
        configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private val jsonMediaType: MediaType = MediaType.parse("application/json")
    private val emptyBody = RequestBody.create(jsonMediaType, "")

    fun post(url: String): RestResult<String> {
        val request = requestBuilder(url)
            .post(emptyBody)
            .build()

        return doRequest(request, { response ->
            response.body().string()
        })
    }

    fun <T : Any, U : Any> post(url: String, body: U, klass: KClass<T>): RestResult<T> {

        val bodyJson = objectMapper.writeValueAsString(body)
        val request = requestBuilder(url)
            .post(RequestBody.create(jsonMediaType, bodyJson))
            .build()

        return doRequest(request, { response ->
            objectMapper.readValue(response.body().byteStream(), klass.java)
        })
    }

    fun get(url: String): RestResult<String> {
        val request = requestBuilder(url)
            .get()
            .build()

        return doRequest(request, { response ->
            response.body().string()
        })
    }

    fun <T : Any> get(url: String, klass: KClass<T>): RestResult<T> {
        val request = requestBuilder(url)
            .get()
            .header("Accept", "application/json")
            .build()

        return doRequest(request, { response ->
            objectMapper.readValue(response.body().byteStream(), klass.java)
        })
    }


    private fun requestBuilder(url: String) = Request.Builder().url(url)

    private fun <T : Any> doRequest(request: Request, successHandler: (Response) -> T): RestResult<T> {

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
