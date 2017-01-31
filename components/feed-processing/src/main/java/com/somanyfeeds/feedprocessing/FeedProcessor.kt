package com.somanyfeeds.feedprocessing

import com.somanyfeeds.articlesdataaccess.ArticleRecord
import com.somanyfeeds.feeddataaccess.FeedRecord

interface FeedProcessor {

    fun canProcess(feed: FeedRecord): Boolean

    fun process(feed: FeedRecord): List<ArticleRecord>
}
