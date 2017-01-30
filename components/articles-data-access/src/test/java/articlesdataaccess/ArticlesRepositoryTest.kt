package articlesdataaccess

import com.somanyfeeds.articlesdataaccess.Article
import com.somanyfeeds.articlesdataaccess.ArticlesRepository
import com.somanyfeeds.datasource.createDataSource
import io.damo.aspen.Test
import io.damo.dropwizard.cloudfoundry.configs.DataSourceConfig
import org.assertj.core.api.Assertions.assertThat
import org.skife.jdbi.v2.DBI
import java.time.LocalDateTime

class ArticlesRepositoryTest : Test({

    val dataSource = createTestDataSource("somanyfeeds_article_test")
    val repo = ArticlesRepository(dataSource)
    val dbi = DBI(dataSource)

    test("#findAll") {
        //language=PostgreSQL
        dbi.execute("DELETE FROM article")

        //language=PostgreSQL
        dbi.execute("""
            INSERT INTO article(id, feed_id, title, link, content, date) VALUES
            (100, 10, 'My First Article', 'http://example.com/blog/articles/1', 'This is a first great article...', '2010-01-02T03:04:05'),
            (101, 10, 'My Second Article', 'http://example.com/blog/articles/2', 'This is a second great article...', '2011-01-02T03:04:05'),
            (102, 11, 'My Other First Article', 'http://example.com/blog/other-articles/1', 'This is another great article...', '2012-01-02T03:04:05')
        """)


        val articles = repo.findAll()


        val expectedArticle = Article(
            id = 102,
            title = "My Other First Article",
            link = "http://example.com/blog/other-articles/1",
            content = "This is another great article...",
            date = LocalDateTime.parse("2012-01-02T03:04:05"),
            source = "my-other-feed"
        )
        assertThat(articles).hasSize(3)
        assertThat(articles.first()).isEqualTo(expectedArticle)
    }
})


fun DBI.execute(sql: String) = this.open().use { it.execute(sql) }

fun createTestDataSource(name: String) = createDataSource(DataSourceConfig(
    serverName = "localhost",
    databaseName = name,
    portNumber = 5432,
    user = "dam5s"
))
