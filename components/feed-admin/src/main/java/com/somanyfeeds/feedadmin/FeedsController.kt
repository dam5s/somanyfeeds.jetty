package com.somanyfeeds.feedadmin

import com.somanyfeeds.feeddataaccess.FeedRepository
import com.somanyfeeds.feeddataaccess.FeedUpdates
import com.somanyfeeds.feeddataaccess.feedTypeFromString
import freemarker.template.Configuration
import freemarker.template.Template
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.util.MultiMap
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

class FeedsController(freemarker: Configuration, val feedRepo: FeedRepository) : AbstractHandler() {

    val feedsTemplate: Template = freemarker.getTemplate("feeds.ftl")
    val showFeedTemplate: Template = freemarker.getTemplate("showFeed.ftl")
    val editFeedTemplate: Template = freemarker.getTemplate("editFeed.ftl")

    val feedDetailsRegex = Regex("^/feeds/(\\d+)$")
    val editRegex = Regex("^/feeds/(\\d+)/edit$")


    override fun handle(target: String, request: Request, servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        if (request.method == "GET" && request.requestURI == "/feeds") {
            val feeds = feedRepo.findAll().map(::present)
            val model = mapOf("feeds" to feeds)

            feedsTemplate.process(model, servletResponse.writer)
            request.isHandled = true
            return
        }

        if (request.method == "GET" && editRegex.matches(request.requestURI)) {
            val matchResult = editRegex.matchEntire(request.requestURI)!!
            val id = matchResult.groupValues[1].toLong()
            val feed = feedRepo.find(id)
            val model = mapOf("feed" to present(feed))

            editFeedTemplate.process(model, servletResponse.writer)
            request.isHandled = true
            return
        }

        if (request.method == "GET" && feedDetailsRegex.matches(request.requestURI)) {
            val matchResult = feedDetailsRegex.matchEntire(request.requestURI)!!
            val id = matchResult.groupValues[1].toLong()
            val feed = feedRepo.find(id)
            val session = servletRequest.getSession(true)

            val model = mapOf(
                "feed" to present(feed),
                "notification" to session.takeAttribute("notification")
            )

            showFeedTemplate.process(model, servletResponse.writer)
            request.isHandled = true
            return
        }

        if (request.method == "POST" && feedDetailsRegex.matches(request.requestURI)) {
            val matchResult = feedDetailsRegex.matchEntire(request.requestURI)!!
            val id = matchResult.groupValues[1].toLong()

            val formParams = request.formParams()

            feedRepo.update(id, FeedUpdates(
                name = formParams.firstValue("name"),
                slug = formParams.firstValue("slug"),
                info = formParams.firstValue("info"),
                type = feedTypeFromString(formParams.firstValue("type"))
            ))

            val session = servletRequest.getSession(true)
            session.setAttribute("notification", "Feed updated successfully")
            servletResponse.sendRedirect("/feeds/$id")
            request.isHandled = true
            return
        }
    }


    private fun Request.formParams(): MultiMap<String> {
        val formParams = MultiMap<String>()
        extractFormParameters(formParams)
        return formParams
    }

    private fun <V> MultiMap<V>.firstValue(name: String) = this[name]!!.first()

    private fun HttpSession.takeAttribute(name: String): Any? {
        val value = getAttribute(name)
        removeAttribute(name)
        return value
    }
}
