package com.somanyfeeds.api

import io.dropwizard.Configuration

class Config : Configuration() {
    var dataSource = DataSourceConfig()
}

class DataSourceConfig {
    var serverName: String = ""
    var databaseName: String = ""
    var portNumber: Int = 0
    var user: String = ""
    var password: String? = null
}
