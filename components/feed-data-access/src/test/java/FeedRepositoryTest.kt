import com.somanyfeeds.cloudfoundry.configs.DataSourceConfig
import com.somanyfeeds.datasource.createDataSource
import com.somanyfeeds.feeddataaccess.FeedRecord
import com.somanyfeeds.feeddataaccess.FeedRepository
import com.somanyfeeds.feeddataaccess.FeedType
import com.somanyfeeds.jdbcsupport.JdbcTemplate
import io.damo.aspen.Test
import org.assertj.core.api.Assertions.assertThat


class FeedRepositoryTest : Test({

    val dataSource = createTestDataSource("somanyfeeds_feed_test")
    val jdbcTemplate = JdbcTemplate(dataSource)
    val repo = FeedRepository(jdbcTemplate)

    before {
        //language=PostgreSQL
        jdbcTemplate.execute("TRUNCATE TABLE feed CASCADE")

        //language=PostgreSQL
        jdbcTemplate.execute("""
            INSERT INTO feed (id, name, slug, info, type) VALUES
            (210, 'G+', 'g-plus', 'http://gplus.example.com/feed.rss', 'RSS'),
            (211, 'Github', 'github', 'http://github.example.com/feed.atom', 'ATOM'),
            (212, 'Tumblr', 'tumblr', 'http://tumb.example.com/feed.rss', 'RSS')
        """)
    }


    test("#findAll") {
        val feeds = repo.findAll()

        val expectedFeeds = listOf(
            FeedRecord(
                id = 210,
                name = "G+",
                slug = "g-plus",
                info = "http://gplus.example.com/feed.rss",
                type = FeedType.RSS
            ),
            FeedRecord(
                id = 211,
                name = "Github",
                slug = "github",
                info = "http://github.example.com/feed.atom",
                type = FeedType.ATOM
            ),
            FeedRecord(
                id = 212,
                name = "Tumblr",
                slug = "tumblr",
                info = "http://tumb.example.com/feed.rss",
                type = FeedType.RSS
            )
        )
        assertThat(feeds).isEqualTo(expectedFeeds)
    }

    test("#find") {
        val feed = repo.find(212)

        val expectedFeed = FeedRecord(
            id = 212,
            name = "Tumblr",
            slug = "tumblr",
            info = "http://tumb.example.com/feed.rss",
            type = FeedType.RSS
        )

        assertThat(feed).isEqualTo(expectedFeed)
    }
})

fun createTestDataSource(name: String) = createDataSource(DataSourceConfig(
    serverName = "localhost",
    databaseName = name,
    portNumber = 5432,
    user = "dam5s"
))
