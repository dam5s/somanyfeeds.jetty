package com.somanyfeeds.api

import com.somanyfeeds.articlesapi.ArticlesResource
import com.somanyfeeds.articlesdataaccess.ArticlesRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.ds.PGSimpleDataSource

class Services(config: Config) {

    private val pg = PGSimpleDataSource().apply {
        val dataSourceConfig = config.dataSourceConfig

        serverName = dataSourceConfig.serverName
        databaseName = dataSourceConfig.databaseName
        portNumber = dataSourceConfig.portNumber
        user = dataSourceConfig.user
        password = dataSourceConfig.password
    }

    private val poolingConfig = HikariConfig().apply {
        dataSource = pg
    }


    val dataSource = HikariDataSource(poolingConfig)
    val articlesRepo = ArticlesRepository(dataSource)
    val articlesResource = ArticlesResource(articlesRepo)
}
