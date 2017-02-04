package com.somanyfeeds.jetty.extensions

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.util.MultiMap
import javax.servlet.http.HttpSession


fun Request.formParams(): MultiMap<String> {
    val formParams = MultiMap<String>()
    extractFormParameters(formParams)
    return formParams
}

fun <V> MultiMap<V>.firstValue(name: String) = this[name]!!.first()!!

fun HttpSession.takeAttribute(name: String): Any? {
    val value = getAttribute(name)
    removeAttribute(name)
    return value
}
