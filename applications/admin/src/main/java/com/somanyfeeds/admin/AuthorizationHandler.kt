package com.somanyfeeds.admin

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthorizationHandler : AbstractHandler() {

    override fun handle(target: String, request: Request, servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {

        if (request.session.getAttribute("login") != "dam5s") {
            servletResponse.status = 403
            servletResponse.addHeader("Content-Type", "text/plain")
            servletResponse.writer.write("Authorization Denied")
            request.isHandled = true
        }
    }
}
