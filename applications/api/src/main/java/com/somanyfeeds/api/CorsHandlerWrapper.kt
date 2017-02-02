package com.somanyfeeds.api

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.HandlerWrapper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CorsHandlerWrapper : HandlerWrapper() {

    override fun handle(target: String, request: Request, servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        servletResponse.addCorsHeaders()

        if (request.method == "OPTIONS") {
            request.isHandled = true
            return
        }

        super.handle(target, request, servletRequest, servletResponse)
    }

    private fun HttpServletResponse.addCorsHeaders() {
        setHeader("Access-Control-Allow-Origin", "*")
        setHeader("Access-Control-Allow-Methods", "OPTIONS,GET")
    }
}
