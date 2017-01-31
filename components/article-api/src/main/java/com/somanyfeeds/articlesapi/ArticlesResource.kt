package com.somanyfeeds.articlesapi

import com.somanyfeeds.articlesdataaccess.ArticlesRepository
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/articles")
@Produces(APPLICATION_JSON)
class ArticlesResource(val repo: ArticlesRepository) {

    @GET
    fun list() = repo.findAll().map(::present)
}
