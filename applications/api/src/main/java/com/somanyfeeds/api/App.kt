package com.somanyfeeds.api

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import java.util.*

class App : Application<Config>() {

    override fun run(config: Config, env: Environment) {
        val services = Services(config)

        env.healthChecks().register("base", BaseHealthCheck())
        env.jersey().register(services.articlesResource)
    }

    override fun initialize(bootstrap: Bootstrap<Config>) {
        bootstrap.objectMapper.registerKotlinModule()
    }
}

fun main(vararg args: String) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().run(*args)
}
