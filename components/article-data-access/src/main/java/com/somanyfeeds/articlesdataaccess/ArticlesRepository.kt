package com.somanyfeeds.articlesdataaccess

import com.somanyfeeds.jdbcsupport.JdbcTemplate
import java.sql.ResultSet
import java.time.LocalDateTime
import javax.sql.DataSource

class ArticlesRepository(dataSource: DataSource) {

    private val jdbcTemplate = JdbcTemplate(dataSource)


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


    private fun ResultSet.getLocalDateTime(column: Int)
        = LocalDateTime.parse(getString(column).replace(" ", "T"))

    private val findAllSQL = """
        SELECT a.id, a.title, a.link, a.content, a.date, f.slug
        FROM article a
        JOIN feed f ON f.id = a.feed_id
        ORDER BY a.date DESC
    """
}
