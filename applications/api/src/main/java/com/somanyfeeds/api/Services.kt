package com.somanyfeeds.api

import com.somanyfeeds.articlesapi.ArticlesController
import com.somanyfeeds.articlesdataaccess.ArticleRepository
import com.somanyfeeds.cloudfoundry.configs.DataSourceConfig
import com.somanyfeeds.cloudfoundry.configs.TwitterConfig
import com.somanyfeeds.datasource.createDataSource
import com.somanyfeeds.feeddataaccess.FeedRepository
import com.somanyfeeds.feedprocessing.ArticleUpdater
import com.somanyfeeds.feedprocessing.FeedsUpdater
import com.somanyfeeds.feedprocessing.atom.AtomFeedProcessor
import com.somanyfeeds.feedprocessing.rss.RssFeedProcessor
import com.somanyfeeds.feedprocessing.twitter.TwitterFeedProcessor
import com.somanyfeeds.jdbcsupport.TransactionalJdbcTemplate
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import java.util.concurrent.ScheduledThreadPoolExecutor

class Services(dataSourceConfig: DataSourceConfig, twitterConfig: TwitterConfig) {
    val dataSource = createDataSource(dataSourceConfig)
    val jdbcTemplate = TransactionalJdbcTemplate(dataSource)
    val transactionManager = jdbcTemplate.transactionManager

    val articlesRepo = ArticleRepository(jdbcTemplate)
    val articlesController = ArticlesController(articlesRepo)


    val twitterFactory = TwitterFactory(ConfigurationBuilder()
        .setOAuthConsumerKey(twitterConfig.consumerKey)
        .setOAuthConsumerSecret(twitterConfig.consumerSecret)
        .setOAuthAccessToken(twitterConfig.accessToken)
        .setOAuthAccessTokenSecret(twitterConfig.accessTokenSecret)
        .build())

    val articleUpdater = ArticleUpdater(articlesRepo, 20, transactionManager)
    val feedsRepo = FeedRepository(jdbcTemplate)
    val feedProcessors = listOf(
        AtomFeedProcessor(),
        RssFeedProcessor(),
        TwitterFeedProcessor(twitterFactory.instance)
    )
    val feedsUpdater = FeedsUpdater(feedsRepo, articleUpdater, feedProcessors)
    val feedUpdatesScheduler = FeedUpdatesScheduler(ScheduledThreadPoolExecutor(2), feedsUpdater)
}
