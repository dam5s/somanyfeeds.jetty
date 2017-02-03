package com.somanyfeeds.jetty

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.session.DatabaseAdaptor
import org.eclipse.jetty.server.session.JDBCSessionDataStore
import org.eclipse.jetty.server.session.JDBCSessionDataStoreFactory
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.util.resource.Resource
import javax.sql.DataSource


abstract class JettyApplication(port: Int) {

    data class JettyAppConfig(
        val services: List<JettyManagedService>,
        val handlers: List<Handler>
    )

    abstract fun configure(): JettyAppConfig

    fun start() {
        val config = configure()

        server.apply {
            config.services.forEach {
                addLifeCycleListener(JettyManagedServiceWrapper(it))
            }

            stopAtShutdown = true
            handler = config.handlers.toList()
            start()
        }
    }


    protected val server = Server(port)

    protected fun buildStaticResourcesHandler()
        = ResourceHandler().apply { baseResource = Resource.newClassPathResource("static") }

    protected fun buildJdbcSessionHandler(dataSource: DataSource): SessionHandler {

        val dbAdaptor = DatabaseAdaptor().apply { datasource = dataSource }

        server.addBean(JDBCSessionDataStoreFactory().apply {
            setDatabaseAdaptor(dbAdaptor)
            setSessionTableSchema(JDBCSessionDataStore.SessionTableSchema().apply {
                setDatabaseAdaptor(dbAdaptor)
            })
        })

        return SessionHandler()
    }

    private fun List<Handler>.toList(): Handler {
        return HandlerList().apply {
            forEach { addHandler(it) }
        }
    }
}
