package com.somanyfeeds.api

import io.dropwizard.Configuration

class Config : Configuration() {
    lateinit var dataSource: DataSourceConfig
        private set
}

data class DataSourceConfig(
    val serverName: String,
    val databaseName: String,
    val portNumber: Int,
    val user: String,
    val password: String?
)
