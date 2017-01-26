package com.somanyfeeds.articlesapi

import com.somanyfeeds.articlesdataaccess.Article
import java.time.LocalDateTime

data class ArticleView(
    val title: String? = null,
    val link: String,
    val content: String,
    val date: LocalDateTime,
    val source: String? = null
)

fun present(article: Article) = ArticleView(
    title = article.title,
    link = article.link,
    content = article.content,
    date = article.date,
    source = article.source
)
