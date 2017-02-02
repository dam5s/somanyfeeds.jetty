package com.somanyfeeds.api

import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener
import org.eclipse.jetty.util.component.LifeCycle
import org.slf4j.LoggerFactory
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class FeedUpdatesScheduler(val scheduledExecutorService: ScheduledExecutorService,
                           val feedsUpdater: Runnable) : AbstractLifeCycleListener() {

    private val logger = LoggerFactory.getLogger(javaClass)


    var future: ScheduledFuture<out Any?>? = null

    override fun lifeCycleStarting(event: LifeCycle) {
        future = scheduledExecutorService.scheduleAtFixedRate({
            try {
                feedsUpdater.run()
            } catch (e: Exception) {
                logger.error("There was an error when updating feeds", e)
            }
        }, 0, 15, TimeUnit.MINUTES)
    }

    override fun lifeCycleStopping(event: LifeCycle) {
        future?.cancel(true)
    }
}
