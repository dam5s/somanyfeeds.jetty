package com.somanyfeeds.jetty

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener
import org.eclipse.jetty.util.component.LifeCycle


abstract class JettyApplication {

    abstract val applicationServices: List<JettyManagedService>
    abstract val applicationHandlers: List<Handler>
    abstract val port: Int

    fun start() {
        Server(port).apply {
            stopAtShutdown = true
            handler = applicationHandlers.toList()

            applicationServices.forEach {
                addLifeCycleListener(JettyManagedServiceWrapper(it))
            }

            start()
        }
    }


    private fun List<Handler>.toList(): Handler {
        return HandlerList().apply {
            forEach { addHandler(it) }
        }
    }
}

class JettyManagedServiceWrapper(val service: JettyManagedService) : AbstractLifeCycleListener() {
    override fun lifeCycleStarting(event: LifeCycle?) = service.start()
    override fun lifeCycleStopping(event: LifeCycle?) = service.stop()
}

interface JettyManagedService {
    fun start()
    fun stop()
}
