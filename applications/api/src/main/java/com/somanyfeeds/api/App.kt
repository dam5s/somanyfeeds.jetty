package com.somanyfeeds.api

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.damo.dropwizard.cloudfoundry.CloudFoundryConfigurationFactoryFactory
import io.damo.dropwizard.cloudfoundry.configs.DataSourceConfig
import io.damo.dropwizard.cloudfoundry.configs.TwitterConfig
import io.damo.dropwizard.cloudfoundry.services.mapPostgresDbConfig
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import java.util.*

class Config(val dataSourceConfig: DataSourceConfig, val twitterConfig: TwitterConfig) : Configuration()

class App : Application<Config>() {

    override fun run(config: Config, env: Environment) {
        val services = Services(config)

        env.healthChecks().register("base", BaseHealthCheck())
        env.healthChecks().register("database", DbHealthCheck(services.dataSource))
        env.lifecycle().manage(services.feedUpdatesScheduler)
        env.jersey().register(services.articlesResource)
    }

    override fun initialize(bootstrap: Bootstrap<Config>) {
        bootstrap.objectMapper.registerKotlinModule()
        bootstrap.configurationFactoryFactory = CloudFoundryConfigurationFactoryFactory({ services ->
            val dataSourceConfig = mapPostgresDbConfig(services)
            val twitterConfig = TwitterConfig(
                consumerKey = env("TWITTER_CONSUMER_KEY"),
                consumerSecret = env("TWITTER_CONSUMER_SECRET"),
                accessToken = env("TWITTER_ACCESS_TOKEN"),
                accessTokenSecret = env("TWITTER_ACCESS_TOKEN_SECRET")
            )

            Config(dataSourceConfig, twitterConfig)
        })
    }

    private fun env(name: String) = System.getenv(name)
}

fun main(vararg args: String) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().run(*args)
}
