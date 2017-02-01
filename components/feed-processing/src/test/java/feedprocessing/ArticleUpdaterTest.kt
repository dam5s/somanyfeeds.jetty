package feedprocessing

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.somanyfeeds.articlesdataaccess.ArticleRepository
import com.somanyfeeds.feedprocessing.ArticleUpdater
import com.somanyfeeds.jdbcsupport.TransactionManager
import io.damo.aspen.Test

class ArticleUpdaterTest : Test({
    test {
        val mockArticleRepo: ArticleRepository = mock()
        val fakeTransactionManager = FakeTransactionManager()

        val articlesUpdater = ArticleUpdater(mockArticleRepo, 2, fakeTransactionManager)
        val feed = buildFeedRecord(id = 90)


        articlesUpdater.updateArticles(listOf(
            buildArticleRecord(id = 103),
            buildArticleRecord(id = 104),
            buildArticleRecord(id = 105)
        ), feed)


        verify(mockArticleRepo).deleteByFeed(feed)
        verify(mockArticleRepo).create(buildArticleRecord(id = 103), feed)
        verify(mockArticleRepo).create(buildArticleRecord(id = 104), feed)
        verifyNoMoreInteractions(mockArticleRepo)
    }
})

class FakeTransactionManager : TransactionManager {
    override fun <T> withTransaction(function: () -> T) = function()
}
