package com.somanyfeeds.api

import com.codahale.metrics.health.HealthCheck
import javax.sql.DataSource

class DbHealthCheck(val dataSource: DataSource) : HealthCheck() {

    override fun check(): Result {
        val conn = dataSource.connection

        try {
            conn
                .createStatement()
                .execute("select count(*) from article")

        } catch (t: Throwable) {
            return Result.unhealthy(t)
        } finally {
            conn.close()
        }

        return Result.healthy()
    }
}
