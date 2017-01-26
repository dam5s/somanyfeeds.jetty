package com.somanyfeeds.api

import com.somanyfeeds.articlesapi.ArticlesResource
import com.somanyfeeds.articlesdataaccess.ArticlesRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.ds.PGSimpleDataSource

class Services(config: Config) {

    private val pg = PGSimpleDataSource().apply {
        serverName = config.dataSource.serverName
        databaseName = config.dataSource.databaseName
        portNumber = config.dataSource.portNumber
        user = config.dataSource.user
        password = config.dataSource.password
    }

    private val poolingConfig = HikariConfig().apply {
        dataSource = pg
    }


    val dataSource = HikariDataSource(poolingConfig)
    val articlesRepo = ArticlesRepository(dataSource)
    val articlesResource = ArticlesResource(articlesRepo)
}
