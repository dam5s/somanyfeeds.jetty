package com.somanyfeeds.api

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.somanyfeeds.api.cf.CloudFoundryConfigurationFactoryFactory
import com.somanyfeeds.api.cf.configs.DataSourceConfig
import com.somanyfeeds.api.cf.configs.mapPostgresDbConfig
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import java.util.*

class Config(val dataSource: DataSourceConfig) : Configuration()

class App : Application<Config>() {

    override fun run(config: Config, env: Environment) {
        val services = Services(config)

        env.healthChecks().register("base", BaseHealthCheck())
        env.healthChecks().register("database", DbHealthCheck(services.dataSource))
        env.jersey().register(services.articlesResource)
    }

    override fun initialize(bootstrap: Bootstrap<Config>) {
        bootstrap.objectMapper.registerKotlinModule()
        bootstrap.configurationFactoryFactory = CloudFoundryConfigurationFactoryFactory({ services ->
            Config(mapPostgresDbConfig(services))
        })
    }
}

fun main(vararg args: String) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().run(*args)
}
