package com.somanyfeeds.jetty.oauth

import com.somanyfeeds.restsupport.RestResult.Error
import com.somanyfeeds.restsupport.RestResult.Success
import com.somanyfeeds.restsupport.RestTemplate
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OauthHandler(val clientId: String, val clientSecret: String) : AbstractHandler() {

    val restTemplate = RestTemplate()

    override fun handle(target: String, request: Request, servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        if (request.method == "GET" && request.requestURI == "/oauth/callback") {
            handleCallback(request, servletResponse)
            return
        }

        val session = request.session
        val accessToken = session.getAttribute("accessToken")

        if (accessToken == null) {
            // save page we try to visit in the session
            displayLoginPage(request, servletResponse)
            return
        }

        // if page to visit is in session
        // redirect to page we try to visit
        // remove it from session

        // else
        // verify token
        // if not valid
        // displayLoginPage
    }

    private fun handleCallback(request: Request, response: HttpServletResponse) {
        val parameterMap = request.parameterMap
        val session = request.session
        val expectedState = session.getAttribute("oauth-authorization-state")
        val requestState = parameterMap["state"]!!.first()

        if (requestState != expectedState) {
            unauthorized(request, response)
            return
        }

        val requestCode = parameterMap["code"]!!.first()
        val tokenUrl = "https://github.com/login/oauth/access_token?client_id=$clientId&client_secret=$clientSecret&code=$requestCode&state=$expectedState"
        val result = restTemplate.post(tokenUrl)

        when (result) {
            is Success<String> -> {
                val accessToken = result.value.split("&").first().replace("access_token=", "")
                session.setAttribute("accessToken", accessToken)
                response.sendRedirect("http://localhost:8081/feeds")
                request.isHandled = true
            }
            is Error -> unauthorized(request, response)
        }
    }

    private fun unauthorized(request: Request, response: HttpServletResponse) {
        response.status = 401
        response.addHeader("Content-Type", "text/plain")
        response.writer.write("Permission Denied")
        request.isHandled = true
    }

    private fun displayLoginPage(request: Request, response: HttpServletResponse) {
        val session = request.session
        val state = UUID.randomUUID().toString()

        session.setAttribute("oauth-authorization-state", state)

        val authorizeUrl = "https://github.com/login/oauth/authorize?client_id=$clientId&state=$state&allow_signup=false"

        val loginPageStream = javaClass.classLoader.getResourceAsStream("templates/login.html")
        val loginPageContent = loginPageStream
            .reader().readText()
            .replace("AUTHORIZE_URL", authorizeUrl)
            .replace("AUTHORIZE_WEBSITE_NAME", "Github")

        response.writer.write(loginPageContent)
        response.addHeader("Content-Type", "text/html")
        request.isHandled = true
    }
}
