package com.somanyfeeds.jetty

import org.eclipse.jetty.util.component.AbstractLifeCycle
import org.eclipse.jetty.util.component.LifeCycle

interface JettyManagedService {
    fun start()
    fun stop()
}

internal class JettyManagedServiceWrapper(val service: JettyManagedService) : AbstractLifeCycle.AbstractLifeCycleListener() {
    override fun lifeCycleStarting(event: LifeCycle?) = service.start()
    override fun lifeCycleStopping(event: LifeCycle?) = service.stop()
}
