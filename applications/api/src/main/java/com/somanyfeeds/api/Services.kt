package com.somanyfeeds.api

import com.somanyfeeds.articlesapi.ArticlesResource
import com.somanyfeeds.articlesdataaccess.ArticleRepository
import com.somanyfeeds.datasource.createDataSource
import com.somanyfeeds.feeddataaccess.FeedRepository
import com.somanyfeeds.feedprocessing.ArticleUpdater
import com.somanyfeeds.feedprocessing.FeedsUpdater
import com.somanyfeeds.feedprocessing.atom.AtomFeedProcessor
import com.somanyfeeds.feedprocessing.rss.RssFeedProcessor
import java.util.concurrent.ScheduledThreadPoolExecutor

class Services(config: Config) {
    val dataSource = createDataSource(config.dataSourceConfig)


    val articlesRepo = ArticleRepository(dataSource)
    val articlesResource = ArticlesResource(articlesRepo)

    val articleUpdater = ArticleUpdater(articlesRepo, 20)
    val feedsRepo = FeedRepository(dataSource)
    val feedProcessors = listOf(
        AtomFeedProcessor(),
        RssFeedProcessor()
    )
    val feedsUpdater = FeedsUpdater(feedsRepo, articleUpdater, feedProcessors)
    val feedUpdatesScheduler = FeedUpdatesScheduler(ScheduledThreadPoolExecutor(2), feedsUpdater)
}
