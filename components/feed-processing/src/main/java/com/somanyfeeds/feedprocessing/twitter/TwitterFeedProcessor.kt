package com.somanyfeeds.feedprocessing.twitter

import com.somanyfeeds.articlesdataaccess.ArticleRecord
import com.somanyfeeds.feeddataaccess.FeedRecord
import com.somanyfeeds.feeddataaccess.FeedType
import com.somanyfeeds.feedprocessing.FeedProcessor
import com.somanyfeeds.feedprocessing.toLocalDateTime
import org.slf4j.LoggerFactory
import twitter4j.Paging
import twitter4j.Status
import twitter4j.Twitter

class TwitterFeedProcessor(twitter: Twitter) : FeedProcessor {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val pageSize = 50
    private val tweetsCountToFetch = 20
    private val timelineResources = twitter.timelines()


    override fun canProcess(feed: FeedRecord) = feed.type == FeedType.TWITTER

    override fun process(feed: FeedRecord): List<ArticleRecord> {
        logger.debug("Processing Feed: {}", feed)

        val twitterHandle = feed.info
        val paging = Paging().apply { count = 1 }
        val statuses = timelineResources.getUserTimeline(twitterHandle, paging)

        while (statuses.filtered().count() < tweetsCountToFetch) {
            statuses.fetchMore(twitterHandle)
        }

        val articles = statuses.filtered().map {
            ArticleRecord(
                link = "https://twitter.com/$twitterHandle/status/${it.id}",
                date = it.createdAt.toLocalDateTime(),
                content = it.text,
                source = feed.slug
            )
        }

        if (logger.isDebugEnabled) {
            logger.debug("Processed #{} articles", articles.count())
        }

        return articles.toList()
    }


    private fun MutableList<Status>.fetchMore(screenName: String) {
        val paging = Paging().apply {
            count = pageSize
            maxId = map { it.id }.min()!! - 1
        }

        val moreTweets = timelineResources.getUserTimeline(screenName, paging)
        addAll(moreTweets)
    }

    private fun List<Status>.filtered() = asSequence().withoutRetweets().withoutReplies().take(tweetsCountToFetch)

    private fun Sequence<Status>.withoutRetweets() = filter { !it.isRetweet }

    private fun Sequence<Status>.withoutReplies() = filter { it.inReplyToScreenName == null }
}
