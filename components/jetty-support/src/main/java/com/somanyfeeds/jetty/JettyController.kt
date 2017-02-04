package com.somanyfeeds.jetty

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class JettyController(body: JettyController.() -> Unit) {

    internal val actions = arrayListOf<JettyAction>()

    init {
        body()
    }

    fun get(uri: String, block: (Request, HttpServletResponse) -> Unit)
        = actions.add(SimpleAction("GET", uri, block))

    fun post(uri: String, block: (Request, HttpServletResponse) -> Unit)
        = actions.add(SimpleAction("POST", uri, block))

    fun get(uri: String, block: (Request, HttpServletResponse, List<String>) -> Unit)
        = actions.add(RegexAction("GET", uri, block))

    fun post(uri: String, block: (Request, HttpServletResponse, List<String>) -> Unit)
        = actions.add(RegexAction("POST", uri, block))
}

internal class SimpleAction(val method: String, val uri: String, val block: (Request, HttpServletResponse) -> Unit) : JettyAction {

    override fun predicate(request: Request)
        = (request.method == method && uri == request.requestURI)

    override fun execute(request: Request, response: HttpServletResponse)
        = block(request, response)
}

internal class RegexAction(val method: String, uri: String, val block: (Request, HttpServletResponse, List<String>) -> Unit) : JettyAction {

    val regex = Regex("^$uri$")

    override fun predicate(request: Request)
        = (request.method == method && regex.matches(request.requestURI))

    override fun execute(request: Request, response: HttpServletResponse) {
        val matchResult = regex.matchEntire(request.requestURI)!!
        val uriValues = matchResult.groupValues

        block(request, response, uriValues)
    }
}

internal interface JettyAction {
    fun predicate(request: Request): Boolean
    fun execute(request: Request, response: HttpServletResponse)
}

class JettyControllerHandler(val controller: JettyController) : AbstractHandler() {

    override fun handle(target: String, request: Request, servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        controller.actions.forEach { action ->
            if (action.predicate(request)) {
                action.execute(request, servletResponse)
                request.isHandled = true
                return
            }
        }
    }
}
