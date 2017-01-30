package com.somanyfeeds.articlesdataaccess

import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.time.LocalDateTime
import javax.sql.DataSource

class ArticlesRepository(dataSource: DataSource) {

    fun findAll(): List<ArticleRecord> {
        return withHandle { handle ->
            handle
                .createQuery(findAllSQL)
                .map(articleMapper)
                .list()
        }
    }


    private val dbi = DBI(dataSource)

    private fun <T> withHandle(block: (Handle) -> T): T = dbi.open().use(block)

    private fun ResultSet.getLocalDateTime(column: Int)
        = LocalDateTime.parse(getString(column).replace(" ", "T"))


    private val articleMapper = ResultSetMapper { index, rs, ctxt ->
        ArticleRecord(
            id = rs.getLong(1),
            title = rs.getString(2),
            link = rs.getString(3),
            content = rs.getString(4),
            date = rs.getLocalDateTime(5),
            source = rs.getString(6)
        )
    }

    private val findAllSQL = """
        SELECT a.id, a.title, a.link, a.content, a.date, f.slug
        FROM article a
        JOIN feed f ON f.id = a.feed_id
        ORDER BY a.date DESC
    """
}
