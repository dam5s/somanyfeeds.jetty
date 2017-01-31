package com.somanyfeeds.api

import com.codahale.metrics.health.HealthCheck

class BaseHealthCheck : HealthCheck() {

    override fun check(): Result = Result.healthy()
}
