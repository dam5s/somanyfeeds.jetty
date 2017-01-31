package com.somanyfeeds.articlesapi

import com.somanyfeeds.articlesdataaccess.ArticleRecord
import java.time.LocalDateTime

data class ArticleView(
    val title: String? = null,
    val link: String,
    val content: String,
    val date: LocalDateTime,
    val source: String? = null
)

fun present(articleRecord: ArticleRecord) = ArticleView(
    title = articleRecord.title,
    link = articleRecord.link,
    content = articleRecord.content,
    date = articleRecord.date,
    source = articleRecord.source
)
