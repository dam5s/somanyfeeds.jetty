package com.somanyfeeds.api

import com.somanyfeeds.cloudfoundry.configs.DataSourceConfig
import com.somanyfeeds.cloudfoundry.configs.TwitterConfig
import com.somanyfeeds.cloudfoundry.readVcapServices
import com.somanyfeeds.cloudfoundry.services.mapPostgresDbConfig
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.HandlerWrapper
import java.util.*

class Config(val port: Int, val dataSourceConfig: DataSourceConfig, val twitterConfig: TwitterConfig)

class App {

    private fun env(name: String) = System.getenv(name)

    private fun loadConfig(): Config {
        val vcapServices = readVcapServices()

        val port = env("PORT").toInt()
        val dataSourceConfig = mapPostgresDbConfig(vcapServices)
        val twitterConfig = TwitterConfig(
            consumerKey = env("TWITTER_CONSUMER_KEY"),
            consumerSecret = env("TWITTER_CONSUMER_SECRET"),
            accessToken = env("TWITTER_ACCESS_TOKEN"),
            accessTokenSecret = env("TWITTER_ACCESS_TOKEN_SECRET")
        )

        return Config(port, dataSourceConfig, twitterConfig)
    }

    fun start() {
        val services = Services(loadConfig())
        val wrappers: List<HandlerWrapper> = listOf(CorsHandlerWrapper())
        val handlers: List<Handler> = listOf(services.articlesController)

        Server(loadConfig().port).apply {
            stopAtShutdown = true
            handler = wrappers.merge().apply {
                handler = handlers.union()
            }

            addLifeCycleListener(services.feedUpdatesScheduler)
            start()
        }
    }

    fun List<HandlerWrapper>.merge(): HandlerWrapper {
        val initialWrapper: HandlerWrapper? = null
        val finalWrapper = this.foldRight(initialWrapper, { currentWrapper, maybePreviousWrapper ->
            maybePreviousWrapper?.let { it.handler = currentWrapper }
            currentWrapper
        })!!

        return finalWrapper
    }

    fun List<Handler>.union(): Handler {
        return HandlerList().apply {
            forEach { addHandler(it) }
        }
    }
}

fun main(vararg args: String) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().start()
}
