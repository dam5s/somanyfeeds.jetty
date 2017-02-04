package com.somanyfeeds.api

import com.somanyfeeds.cloudfoundry.configs.TwitterConfig
import com.somanyfeeds.cloudfoundry.readVcapServices
import com.somanyfeeds.cloudfoundry.services.mapPostgresDbConfig
import com.somanyfeeds.jetty.JettyApplication
import com.somanyfeeds.jetty.JettyControllerHandler
import java.util.*

class App(port: Int) : JettyApplication(port) {

    override fun configure(): JettyAppConfig {
        val vcapServices = readVcapServices()
        val dataSourceConfig = mapPostgresDbConfig(vcapServices)
        val twitterConfig = loadTwitterConfig()

        val services = Services(dataSourceConfig, twitterConfig)

        return JettyAppConfig(
            services = listOf(services.feedUpdatesScheduler),
            handlers = listOf(
                CorsHandler(),
                JettyControllerHandler(services.articlesController)
            )
        )
    }


    private fun loadTwitterConfig() = TwitterConfig(
        consumerKey = env("TWITTER_CONSUMER_KEY"),
        consumerSecret = env("TWITTER_CONSUMER_SECRET"),
        accessToken = env("TWITTER_ACCESS_TOKEN"),
        accessTokenSecret = env("TWITTER_ACCESS_TOKEN_SECRET")
    )
}


fun main(vararg args: String) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App(env("PORT").toInt()).start()
}


private fun env(name: String) = System.getenv(name)
