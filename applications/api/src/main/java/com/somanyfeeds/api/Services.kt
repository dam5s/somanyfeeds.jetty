package com.somanyfeeds.api

import com.somanyfeeds.articlesapi.ArticlesResource
import com.somanyfeeds.articlesdataaccess.ArticlesRepository
import com.somanyfeeds.datasource.createDataSource

class Services(config: Config) {
    val dataSource = createDataSource(config.dataSourceConfig)
    val articlesRepo = ArticlesRepository(dataSource)
    val articlesResource = ArticlesResource(articlesRepo)
}
