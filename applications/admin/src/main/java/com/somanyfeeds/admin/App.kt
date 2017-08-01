package com.somanyfeeds.admin

import com.somanyfeeds.cloudfoundry.readVcapServices
import com.somanyfeeds.cloudfoundry.services.mapPostgresDbConfig
import com.somanyfeeds.jetty.JettyApplication
import com.somanyfeeds.jetty.JettyControllerHandler
import com.somanyfeeds.jetty.oauth.OauthHandler
import java.util.*

class App(port: Int) : JettyApplication(port) {

    override fun configure(): JettyAppConfig {

        val vcapServices = readVcapServices()
        val dataSourceConfig = mapPostgresDbConfig(vcapServices)
        val services = Services(dataSourceConfig)

        return JettyAppConfig(
            services = emptyList(),
            handlers = listOf(
                buildClasspathResourcesHandler(),
                buildJdbcSessionHandler(services.dataSource),
                OauthHandler(env("GITHUB_CLIENT_ID"), env("GITHUB_CLIENT_SECRET")),
                AuthorizationHandler(),
                JettyControllerHandler(services.mainController),
                JettyControllerHandler(services.feedsController)
            )
        )
    }
}

fun main(vararg args: String) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App(env("PORT").toInt()).start()
}

fun env(name: String): String
    = System.getenv(name) ?: throw IllegalStateException("ENV variable $name is missing")
