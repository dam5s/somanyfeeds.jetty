package com.somanyfeeds.api

import com.somanyfeeds.jetty.JettyManagedService
import org.slf4j.LoggerFactory
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class FeedUpdatesScheduler(val scheduledExecutorService: ScheduledExecutorService,
                           val feedsUpdater: Runnable) : JettyManagedService {

    private val logger = LoggerFactory.getLogger(javaClass)


    var future: ScheduledFuture<out Any?>? = null

    override fun start() {
        future = scheduledExecutorService.scheduleAtFixedRate({
            try {
                feedsUpdater.run()
            } catch (e: Exception) {
                logger.error("There was an error when updating feeds", e)
            }
        }, 0, 15, TimeUnit.MINUTES)
    }

    override fun stop() {
        future?.cancel(true)
    }
}
