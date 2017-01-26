package com.somanyfeeds.api

import com.somanyfeeds.articlesapi.ArticlesResource
import com.somanyfeeds.articlesdataaccess.ArticlesRepository
import io.dropwizard.Application
import io.dropwizard.setup.Environment

class App : Application<Config>() {

    val articlesRepo = ArticlesRepository()
    val articlesResource = ArticlesResource(articlesRepo)


    override fun run(config: Config, env: Environment) {
        env.healthChecks().register("base", HealthCheck())
        env.jersey().register(articlesResource)
    }
}

fun main(vararg args: String) {
    App().run(*args)
}
