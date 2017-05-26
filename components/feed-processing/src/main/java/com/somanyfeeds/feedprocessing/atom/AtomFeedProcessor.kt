package com.somanyfeeds.feedprocessing.atom

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.somanyfeeds.articlesdataaccess.ArticleRecord
import com.somanyfeeds.feeddataaccess.FeedRecord
import com.somanyfeeds.feeddataaccess.FeedType
import com.somanyfeeds.feedprocessing.FeedProcessor
import com.somanyfeeds.feedprocessing.toLocalDateTime
import com.somanyfeeds.restsupport.RestResult
import com.somanyfeeds.restsupport.RestResult.Error
import com.somanyfeeds.restsupport.RestResult.Success
import com.somanyfeeds.restsupport.RestTemplate
import org.slf4j.LoggerFactory
import java.io.IOException

class AtomFeedProcessor(val restTemplate: RestTemplate = RestTemplate()) : FeedProcessor {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val xmlMapper = XmlMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }


    override fun canProcess(feed: FeedRecord) = (feed.type == FeedType.ATOM)

    override fun process(feed: FeedRecord): List<ArticleRecord> {
        logger.debug("Processing Feed: {}", feed)

        val result = restTemplate.get(feed.info)

        when (result) {
            is Success -> return processAtomContent(feed, result.value)
            is Error -> throw IOException("There was an error fetching the feed $feed, ${result.error}")
        }
    }


    private fun processAtomContent(feed: FeedRecord, content: String): List<ArticleRecord> {
        val atom = xmlMapper.readValue(content, Atom::class.java)
        val articles = atom.entries.map {
            ArticleRecord(
                title = it.title.text,
                link = it.link.href,
                date = it.published.toLocalDateTime(),
                content = it.content.text,
                source = feed.slug
            )
        }

        logger.debug("Processed #{} articles", articles.size)
        return articles
    }
}
