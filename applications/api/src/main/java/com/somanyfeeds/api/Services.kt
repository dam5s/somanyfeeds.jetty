package com.somanyfeeds.api

import com.somanyfeeds.articlesapi.ArticlesResource
import com.somanyfeeds.articlesdataaccess.ArticleRepository
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

class Services(config: Config) {
    val dataSource = createDataSource(config.dataSourceConfig)
    val jdbcTemplate = TransactionalJdbcTemplate(dataSource)
    val transactionManager = jdbcTemplate.transactionManager

    val articlesRepo = ArticleRepository(jdbcTemplate)
    val articlesResource = ArticlesResource(articlesRepo)


    val twitterFactory = TwitterFactory(ConfigurationBuilder()
        .setOAuthConsumerKey(config.twitterConfig.consumerKey)
        .setOAuthConsumerSecret(config.twitterConfig.consumerSecret)
        .setOAuthAccessToken(config.twitterConfig.accessToken)
        .setOAuthAccessTokenSecret(config.twitterConfig.accessTokenSecret)
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
