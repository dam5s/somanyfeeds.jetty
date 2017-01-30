package com.somanyfeeds.datasource

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.damo.dropwizard.cloudfoundry.configs.DataSourceConfig
import org.postgresql.ds.PGSimpleDataSource
import javax.sql.DataSource

fun createDataSource(config: DataSourceConfig): DataSource {
    val pg = PGSimpleDataSource().apply {
        serverName = config.serverName
        databaseName = config.databaseName
        portNumber = config.portNumber
        user = config.user
        password = config.password
    }

    val poolingConfig = HikariConfig().apply {
        dataSource = pg
    }

    return HikariDataSource(poolingConfig)
}
