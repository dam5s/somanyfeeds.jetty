package com.somanyfeeds.api.cf.configs

data class DataSourceConfig(
    val serverName: String,
    val databaseName: String,
    val portNumber: Int,
    val user: String,
    val password: String?
)
