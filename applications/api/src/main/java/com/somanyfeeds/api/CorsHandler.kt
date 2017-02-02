package com.somanyfeeds.api

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CorsHandler : AbstractHandler() {

    override fun handle(target: String, request: Request, servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        servletResponse.addCorsHeaders()

        if (request.method == "OPTIONS") {
            request.isHandled = true
            return
        }
    }

    private fun HttpServletResponse.addCorsHeaders() {
        setHeader("Access-Control-Allow-Origin", "*")
        setHeader("Access-Control-Allow-Methods", "OPTIONS,GET")
    }
}
