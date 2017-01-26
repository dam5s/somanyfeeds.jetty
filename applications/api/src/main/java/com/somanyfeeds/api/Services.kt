package com.somanyfeeds.api

import com.somanyfeeds.articlesapi.ArticlesResource
import com.somanyfeeds.articlesdataaccess.ArticlesRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.postgresql.ds.PGSimpleDataSource

class Services(config: Config) {

    val pgDataSource = PGSimpleDataSource().apply {
        serverName = config.dataSource.serverName
        databaseName = config.dataSource.databaseName
        portNumber = config.dataSource.portNumber
        user = config.dataSource.user
        password = config.dataSource.password
    }

    val poolingConfig = HikariConfig().apply {
        dataSource = pgDataSource
    }

    val poolingDataSource = HikariDataSource(poolingConfig)


    val articlesRepo = ArticlesRepository(poolingDataSource)
    val articlesResource = ArticlesResource(articlesRepo)
}
