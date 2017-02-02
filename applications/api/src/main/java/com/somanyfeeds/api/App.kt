package com.somanyfeeds.api

import com.somanyfeeds.cloudfoundry.configs.TwitterConfig
import com.somanyfeeds.cloudfoundry.readVcapServices
import com.somanyfeeds.cloudfoundry.services.mapPostgresDbConfig
import com.somanyfeeds.jetty.JettyApplication
import com.somanyfeeds.jetty.JettyManagedService
import org.eclipse.jetty.server.Handler
import java.util.*

class App : JettyApplication() {

    override val applicationServices: List<JettyManagedService>
    override val applicationHandlers: List<Handler>
    override val port: Int

    init {
        val vcapServices = readVcapServices()
        val dataSourceConfig = mapPostgresDbConfig(vcapServices)
        val twitterConfig = loadTwitterConfig()

        val services = Services(dataSourceConfig, twitterConfig)

        port = env("PORT").toInt()
        applicationServices = listOf(
            services.feedUpdatesScheduler
        )
        applicationHandlers = listOf(
            CorsHandler(),
            services.articlesController
        )
    }


    private fun env(name: String) = System.getenv(name)

    private fun loadTwitterConfig(): TwitterConfig {
        return TwitterConfig(
            consumerKey = env("TWITTER_CONSUMER_KEY"),
            consumerSecret = env("TWITTER_CONSUMER_SECRET"),
            accessToken = env("TWITTER_ACCESS_TOKEN"),
            accessTokenSecret = env("TWITTER_ACCESS_TOKEN_SECRET")
        )
    }
}

fun main(vararg args: String) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().start()
}
