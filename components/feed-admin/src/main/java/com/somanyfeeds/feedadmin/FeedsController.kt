package com.somanyfeeds.feedadmin

import com.somanyfeeds.feeddataaccess.FeedRepository
import freemarker.template.Configuration
import freemarker.template.Template
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FeedsController(freemarker: Configuration, val feedRepo: FeedRepository) : AbstractHandler() {

    val feedsTemplate: Template = freemarker.getTemplate("feeds.ftl")
    val editFeedTemplate: Template = freemarker.getTemplate("editFeed.ftl")
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
    }
}
