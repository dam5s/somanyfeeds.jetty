package com.somanyfeeds.api

import com.codahale.metrics.health.HealthCheck
import kotlinx.support.jdk7.use
import javax.sql.DataSource

class DbHealthCheck(val dataSource: DataSource) : HealthCheck() {

    override fun check(): Result {
        dataSource.connection.use { connection ->
            try {
                connection
                    .createStatement()
                    .execute("select count(*) from article")
            } catch (t: Throwable) {
                return Result.unhealthy(t)
            }
        }

        return Result.healthy()
    }
}
