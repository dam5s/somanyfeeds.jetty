package com.somanyfeeds.feedprocessing.rss

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.somanyfeeds.articlesdataaccess.ArticleRecord
import com.somanyfeeds.feeddataaccess.FeedRecord
import com.somanyfeeds.feeddataaccess.FeedType
import com.somanyfeeds.feedprocessing.FeedProcessor
import com.somanyfeeds.feedprocessing.toLocalDateTime
import com.somanyfeeds.restsupport.RestResult.Error
import com.somanyfeeds.restsupport.RestResult.Success
import com.somanyfeeds.restsupport.RestTemplate
import org.slf4j.LoggerFactory
import java.io.IOException

class RssFeedProcessor(val restTemplate: RestTemplate = RestTemplate()) : FeedProcessor {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val xmlMapper = XmlMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }


    override fun canProcess(feed: FeedRecord) = (feed.type == FeedType.RSS)

    override fun process(feed: FeedRecord): List<ArticleRecord> {
        logger.debug("Processing Feed: {}", feed)

        val result = restTemplate.get(feed.info)

        when (result) {
            is Success -> return processRssContent(feed, result.value)
            is Error -> throw IOException("There was an error fetching the feed $feed, ${result.error}")
        }
    }


    private fun processRssContent(feed: FeedRecord, content: String): List<ArticleRecord> {
        val rssString = content.replace("\uFEFF", "")
        val rss = xmlMapper.readValue(rssString, Rss::class.java)

        val articles = rss.channel.items.map {
            ArticleRecord(
                title = it.title,
                link = it.link,
                date = it.pubDate.toLocalDateTime(),
                content = getContent(it),
                source = feed.slug
            )
        }

        logger.debug("Processed #{} articles", articles.size)
        return articles
    }

    private fun getContent(item: Item): String {
        var content = item.description

        if (content.isEmpty()) {
            content = item.encoded
        }

        return content
    }
}
