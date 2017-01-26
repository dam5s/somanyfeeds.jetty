package com.somanyfeeds.api

import com.codahale.metrics.health.HealthCheck

class HealthCheck : HealthCheck() {

    override fun check() = Result.healthy()
}
