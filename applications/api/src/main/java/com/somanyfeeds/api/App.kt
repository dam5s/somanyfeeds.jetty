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
import org.eclipse.jetty.servlets.CrossOriginFilter
import org.eclipse.jetty.servlets.CrossOriginFilter.*
import java.util.*
import java.util.EnumSet.allOf
import javax.servlet.DispatcherType

class Config(val dataSourceConfig: DataSourceConfig, val twitterConfig: TwitterConfig) : Configuration()

class App : Application<Config>() {

    override fun run(config: Config, env: Environment) {
        val services = Services(config)

        env.healthChecks().register("base", BaseHealthCheck())
        env.healthChecks().register("database", DbHealthCheck(services.dataSource))
        env.lifecycle().manage(services.feedUpdatesScheduler)
        env.jersey().register(services.articlesResource)

        env.servlets().addFilter("CORS", CrossOriginFilter::class.java).apply {
            setInitParameter(ALLOWED_ORIGINS_PARAM, "*");
            setInitParameter(ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
            setInitParameter(ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
            setInitParameter(ALLOW_CREDENTIALS_PARAM, "true");
            addMappingForUrlPatterns(allOf(DispatcherType::class.java), true, "/articles");
        }
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
