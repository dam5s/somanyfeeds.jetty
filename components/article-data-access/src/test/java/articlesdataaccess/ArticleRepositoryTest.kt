package articlesdataaccess

import com.somanyfeeds.articlesdataaccess.ArticleRecord
import com.somanyfeeds.articlesdataaccess.ArticleRepository
import com.somanyfeeds.datasource.createDataSource
import com.somanyfeeds.feeddataaccess.FeedRecord
import com.somanyfeeds.feeddataaccess.FeedType
import com.somanyfeeds.jdbcsupport.JdbcTemplate
import io.damo.aspen.Test
import io.damo.dropwizard.cloudfoundry.configs.DataSourceConfig
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime

class ArticleRepositoryTest : Test({

    val dataSource = createTestDataSource("somanyfeeds_article_test")
    val repo = ArticleRepository(dataSource)
    val jdbcTemplate = JdbcTemplate(dataSource)

    before {
        //language=PostgreSQL
        jdbcTemplate.execute("TRUNCATE TABLE feed CASCADE")

        //language=PostgreSQL
        jdbcTemplate.execute("""
          INSERT INTO feed(id, name, slug, info, type) VALUES
          (10, 'My Feed', 'my-feed', 'http://example.com/feed.rss', 'RSS'),
          (11, 'My Other Feed', 'my-other-feed', 'http://example.com/other-feed.atom', 'ATOM'),
          (12, 'My Last Feed', 'my-last-feed', 'http://example.com/last-feed.atom', 'ATOM')
        """)
    }

    test("#findAll") {
        //language=PostgreSQL
        jdbcTemplate.execute("""
            INSERT INTO article(id, feed_id, title, link, content, date) VALUES
            (100, 10, 'My First ArticleRecord', 'http://example.com/blog/articles/1', 'This is a first great article...', '2010-01-02T03:04:05'),
            (101, 10, 'My Second ArticleRecord', 'http://example.com/blog/articles/2', 'This is a second great article...', '2011-01-02T03:04:05'),
            (102, 11, 'My Other First ArticleRecord', 'http://example.com/blog/other-articles/1', 'This is another great article...', '2012-01-02T03:04:05')
        """)


        val articles = repo.findAll()


        val expectedArticle = ArticleRecord(
            id = 102,
            title = "My Other First ArticleRecord",
            link = "http://example.com/blog/other-articles/1",
            content = "This is another great article...",
            date = LocalDateTime.parse("2012-01-02T03:04:05"),
            source = "my-other-feed"
        )
        assertThat(articles).hasSize(3)
        assertThat(articles.first()).isEqualTo(expectedArticle)
    }

    test("#create") {
        val feed = buildFeedRecord(id = 10)
        val article = ArticleRecord(
            title = "My Article",
            link = "http://example.com/my/article",
            content = "It's great and stuff...",
            date = LocalDateTime.parse("2010-01-02T03:04:05"),
            source = null
        )


        val created = repo.create(article, feed)


        assertThat(jdbcTemplate.count("SELECT COUNT(*) FROM article WHERE id = $created")).isEqualTo(1L)

        jdbcTemplate.query("SELECT * FROM article WHERE id = $created") { rs ->
            assertThat(rs.getLong("id")).isEqualTo(created)
            assertThat(rs.getLong("feed_id")).isEqualTo(10L)
            assertThat(rs.getString("title")).isEqualTo("My Article")
            assertThat(rs.getString("link")).isEqualTo("http://example.com/my/article")
            assertThat(rs.getString("content")).isEqualTo("It's great and stuff...")
            assertThat(rs.getString("date")).isEqualTo("2010-01-02 03:04:05")
        }
    }

    test("#deleteByFeed") {
        //language=PostgreSQL
        jdbcTemplate.execute("""
            INSERT INTO article(id, feed_id, title, link, content, date) VALUES
            (20, 10, 'My Article 1', 'http://example.com/article1', 'content 1...', '2010-01-02T03:04:05Z'),
            (21, 10, 'My Article 2', 'http://example.com/article2', 'content 2...', '2010-01-02T03:04:05Z'),
            (22, 11, 'My Article 3', 'http://example.com/article3', 'content 3...', '2010-01-02T03:04:05Z')
        """)


        repo.deleteByFeed(buildFeedRecord(id = 10))


        assertThat(jdbcTemplate.count("SELECT COUNT(*) FROM article")).isEqualTo(1L)

        jdbcTemplate.query("SELECT * FROM article") { rs ->
            assertThat(rs.getLong("id")).isEqualTo(22L)
            assertThat(rs.getLong("feed_id")).isEqualTo(11L)
            assertThat(rs.getString("title")).isEqualTo("My Article 3")
            assertThat(rs.getString("link")).isEqualTo("http://example.com/article3")
            assertThat(rs.getString("content")).isEqualTo("content 3...")
            assertThat(rs.getString("date")).isEqualTo("2010-01-02 03:04:05")
        }
    }
})

private fun buildFeedRecord(
    id: Long? = 10,
    name: String = "My Feed",
    slug: String = "my-feed",
    info: String = "http://example.com/feed.rss",
    type: FeedType = FeedType.RSS
) = FeedRecord(id, name, slug, info, type)

fun createTestDataSource(name: String) = createDataSource(DataSourceConfig(
    serverName = "localhost",
    databaseName = name,
    portNumber = 5432,
    user = "dam5s"
))
