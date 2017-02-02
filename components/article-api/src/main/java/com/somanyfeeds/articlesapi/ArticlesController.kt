package com.somanyfeeds.articlesapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.somanyfeeds.articlesdataaccess.ArticleRepository
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ArticlesController(val repo: ArticleRepository) : AbstractHandler() {

    val objectMapper = ObjectMapper().registerKotlinModule()

    override fun handle(target: String, request: Request, servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        if (request.method == "GET" && request.requestURI == "/articles") {
            servletResponse.contentType = "application/json"
            servletResponse.status = HttpServletResponse.SC_OK

            val responseBody = mapOf("articles" to allArticleViews())

            objectMapper.writeValue(servletResponse.outputStream, responseBody)
            request.isHandled = true
        }
    }

    private fun allArticleViews() = repo.findAll().map(::present)
}
