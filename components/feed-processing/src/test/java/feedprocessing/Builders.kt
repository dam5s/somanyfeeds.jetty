package feedprocessing

import com.somanyfeeds.articlesdataaccess.ArticleRecord
import com.somanyfeeds.feeddataaccess.FeedRecord
import com.somanyfeeds.feeddataaccess.FeedType
import java.time.LocalDateTime

fun buildArticleRecord(
    id: Long? = null,
    title: String = "My Article",
    link: String = "http://example.com/articles/my-article",
    content: String = "Hello World",
    date: LocalDateTime = LocalDateTime.parse("2010-01-02T03:04:05"),
    source: String = "Blog"
) = ArticleRecord(id, title, link, content, date, source)

fun buildFeedRecord(
    id: Long? = null,
    name: String = "My Feed",
    slug: String = "my-feed",
    url: String = "http://example.com/feed/rss",
    type: FeedType = FeedType.RSS
) = FeedRecord(id, name, slug, url, type)
