package com.somanyfeeds.articlesapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.somanyfeeds.articlesdataaccess.ArticleRepository
import com.somanyfeeds.jetty.JettyController
import javax.servlet.http.HttpServletResponse

class ArticlesController(val repo: ArticleRepository) : JettyController({

    val objectMapper = jacksonObjectMapper()

    get("/articles") { request, response ->
        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_OK

        val allArticleViews = repo.findAll().map(::present)
        val responseBody = mapOf("articles" to allArticleViews)

        objectMapper.writeValue(response.outputStream, responseBody)
        request.isHandled = true
    }
})
