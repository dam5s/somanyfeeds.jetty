package com.somanyfeeds.cloudfoundry.configs

data class DataSourceConfig(
    val serverName: String,
    val databaseName: String,
    val portNumber: Int,
    val user: String,
    val password: String? = null
)
