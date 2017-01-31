package com.somanyfeeds.feedprocessing

import com.somanyfeeds.articlesdataaccess.ArticleRecord
import com.somanyfeeds.articlesdataaccess.ArticleRepository
import com.somanyfeeds.feeddataaccess.FeedRecord

class ArticleUpdater(val articleRepo: ArticleRepository,
                     val articleLimit: Int) {

    fun updateArticles(articles: List<ArticleRecord>, feed: FeedRecord) {
        articleRepo.deleteByFeed(feed)

        var count = 0
        for (article in articles) {
            if (count >= articleLimit) break

            articleRepo.create(article, feed)
            count++
        }
    }
}
