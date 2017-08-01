package com.somanyfeeds.jetty.oauth

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.somanyfeeds.jetty.extensions.takeAttribute
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
        val session = request.session

        if (request.method == "GET" && request.requestURI == "/logout") {
            logout(request, servletResponse)
            return
        }

        if (request.method == "GET" && request.requestURI == "/oauth/callback") {
            handleCallback(request, servletResponse)
            return
        }

        if (request.method == "GET" && request.requestURI == "/login") {
            displayLoginPage(request, servletResponse)
            return
        }

        if (!isLoggedIn(request)) {
            if (request.method == "GET") {
                session.setAttribute("requestedURI", request.requestURI)
            }

            servletResponse.sendRedirect("/login")
            request.isHandled = true
            return
        }

        val requestedURI = session.takeAttribute("requestedURI") as? String
        if (requestedURI != null) {
            servletResponse.sendRedirect(requestedURI)
            request.isHandled = true
        }
    }

    private fun isLoggedIn(request: Request): Boolean {
        val session = request.session
        return session.getAttribute("accessToken") != null
    }

    private fun logout(request: Request, servletResponse: HttpServletResponse) {
        request.session.removeAttribute("accessToken")
        servletResponse.sendRedirect("/")
    }

    private fun handleCallback(request: Request, response: HttpServletResponse) {
        val parameterMap = request.parameterMap
        val session = request.session
        val expectedState = session.getAttribute("oauth-authorization-state")
        val requestState = parameterMap["state"]!!.first()

        if (requestState != expectedState) {
            authenticationFailed(request, response)
            return
        }

        val requestCode = parameterMap["code"]!!.first()
        val tokenUrl = "https://github.com/login/oauth/access_token?client_id=$clientId&client_secret=$clientSecret&code=$requestCode&state=$expectedState"
        val result = restTemplate.post(tokenUrl)

        when (result) {
            is Success<String> -> {
                val accessToken = result.value.split("&").first().replace("access_token=", "")
                session.setAttribute("accessToken", accessToken)
                session.setAttribute("login", fetchUsername(accessToken))
                response.sendRedirect("/")
                request.isHandled = true
            }
            is Error -> authenticationFailed(request, response)
        }
    }

    val objectMapper = jacksonObjectMapper().apply {
        configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private fun fetchUsername(accessToken: String): String {
        val userResult = restTemplate.get("https://api.github.com/user?access_token=$accessToken")
        when (userResult) {
            is Success<String> -> {
                return objectMapper
                    .readTree(userResult.value)
                    .get("login")
                    .textValue()
            }
            is Error -> return ""
        }
    }

    private fun authenticationFailed(request: Request, response: HttpServletResponse) {
        response.status = 401
        response.addHeader("Content-Type", "text/plain")
        response.writer.write("Authentication Failed")
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
