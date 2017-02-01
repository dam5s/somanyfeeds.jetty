package com.somanyfeeds.articlesdataaccess

import com.somanyfeeds.feeddataaccess.FeedRecord
import com.somanyfeeds.jdbcsupport.JdbcTemplate
import java.sql.ResultSet
import java.time.LocalDateTime

class ArticleRepository(val jdbcTemplate: JdbcTemplate) {


    fun findAll() = jdbcTemplate.query(findAllSQL) { rs ->
        ArticleRecord(
            id = rs.getLong(1),
            title = rs.getString(2),
            link = rs.getString(3),
            content = rs.getString(4),
            date = rs.getLocalDateTime(5),
            source = rs.getString(6)
        )
    }

    fun create(article: ArticleRecord, feed: FeedRecord) = jdbcTemplate.create("article", mapOf(
        "feed_id" to feed.id,
        "title" to article.title,
        "link" to article.link,
        "content" to article.content,
        "date" to article.date
    ))

    fun deleteByFeed(feed: FeedRecord)
        = jdbcTemplate.execute("DELETE FROM article WHERE feed_id = ?", feed.id!!)


    private fun ResultSet.getLocalDateTime(column: Int)
        = LocalDateTime.parse(getString(column).replace(" ", "T"))

    private val findAllSQL = """
        SELECT a.id, a.title, a.link, a.content, a.date, f.slug
        FROM article a
        JOIN feed f ON f.id = a.feed_id
        ORDER BY a.date DESC
    """
}
