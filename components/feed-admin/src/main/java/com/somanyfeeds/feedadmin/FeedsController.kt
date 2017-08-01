package com.somanyfeeds.feedadmin

import com.somanyfeeds.feeddataaccess.FeedRepository
import com.somanyfeeds.feeddataaccess.FeedUpdates
import com.somanyfeeds.feeddataaccess.feedTypeFromString
import com.somanyfeeds.jetty.JettyController
import com.somanyfeeds.jetty.extensions.firstValue
import com.somanyfeeds.jetty.extensions.formParams
import com.somanyfeeds.jetty.extensions.takeAttribute
import freemarker.template.Configuration
import freemarker.template.Template
import javax.servlet.ServletResponse

class FeedsController(freemarker: Configuration, val feedRepo: FeedRepository) : JettyController({

    val feedsTemplate = freemarker.getTemplate("feeds.ftl")
    val showFeedTemplate = freemarker.getTemplate("showFeed.ftl")
    val editFeedTemplate = freemarker.getTemplate("editFeed.ftl")


    get("/feeds") { _, response ->
        val feeds = feedRepo.findAll().map(::present)
        val model = mapOf("feeds" to feeds)

        response.render(feedsTemplate, model)
    }

    get("/feeds/(\\d+)/edit") { _, response, uriValues ->
        val id = uriValues[1].toLong()
        val feed = feedRepo.find(id)
        val model = mapOf("feed" to present(feed))

        response.render(editFeedTemplate, model)
    }

    get("/feeds/(\\d+)") { request, response, uriValues ->
        val id = uriValues[1].toLong()
        val feed = feedRepo.find(id)

        val model = mapOf(
            "feed" to present(feed),
            "notification" to request.session.takeAttribute("notification")
        )

        response.render(showFeedTemplate, model)
    }

    post("/feeds/(\\d+)") { request, response, uriValues ->
        val id = uriValues[1].toLong()

        val formParams = request.formParams()

        feedRepo.update(id, FeedUpdates(
            name = formParams.firstValue("name"),
            slug = formParams.firstValue("slug"),
            info = formParams.firstValue("info"),
            type = feedTypeFromString(formParams.firstValue("type"))
        ))

        request.session.setAttribute("notification", "Feed updated successfully")
        response.sendRedirect("/feeds/$id")
    }
})

fun ServletResponse.render(template: Template, model: Map<String, Any?>) {
    template.process(model, writer)
}
