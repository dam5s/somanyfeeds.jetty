package com.somanyfeeds.articlesdataaccess

import java.time.LocalDateTime

data class ArticleRecord(
    val id: Long? = null,
    val title: String? = null,
    val link: String,
    val content: String,
    val date: LocalDateTime,
    val source: String? = null
)

fun foo() {

    val myArticle = ArticleRecord(link = "http://example.com", content = "Hello", date = LocalDateTime.now())

    val newArticle = myArticle.copy(content = "Hello world")


}

fun buildArticleRecord(
    id: Long? = null,
    title: String? = null,
    link: String = "http://example.com",
    content: String = "Hello",
    date: LocalDateTime = LocalDateTime.now(),
    source: String? = null
): ArticleRecord = ArticleRecord(id, title, link, content, date)




